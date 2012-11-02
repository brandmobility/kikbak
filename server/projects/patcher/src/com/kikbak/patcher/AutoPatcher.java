package com.kikbak.patcher;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kikbak.db.AbstractDataSourceFactory;
import com.kikbak.db.DBCPDataSourceFactory;
import com.tacitknowledge.util.migration.MigrationException;
import com.tacitknowledge.util.migration.RollbackableMigrationTask;
import com.tacitknowledge.util.migration.jdbc.DataSourceMigrationContext;
import com.tacitknowledge.util.migration.jdbc.DatabaseType;
import com.tacitknowledge.util.migration.jdbc.JdbcMigrationLauncher;


public class AutoPatcher {
    /** Class logger */
    static Log log = LogFactory.getLog(AutoPatcher.class);

    /** The name of the schema to patch */
    private String systemName = null;
    
    /** The names of the patcher modules when there are multiple */
    private String moduleNames = null;

    /** The type of database */
    private String databaseType = null;

    /** The path to the SQL patches */
    private String patchPath = null;

    /** The driver class name **/
    private String driverClassName = null;

    /** The database connection URL **/
    private String url = null;

    /** The database connection username **/
    private String username = null;

    /** The database connection password **/
    private String password = null;

    /**
     * @return Returns the systemName.
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * @param systemName
     *            The systemName to set.
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    
    /** Return the names of the patcher modules when there are multiple */
    public String getModuleNames() {
        return moduleNames;
    }

    /** Sets the names of the patcher modules when there are multiple */
    public void setModuleNames(final String moduleNames) {
        this.moduleNames = moduleNames;
    }

    /**
     * @return the databaseType.
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * @param dialect
     *            The databaseType to set.
     */
    public void setDatabaseType(String dialect) {
        this.databaseType = dialect;
    }

    /**
     * @return Returns the patchPath.
     */
    public String getPatchPath() {
        return patchPath;
    }

    /**
     * @param patchPath
     *            The patchPath to set.
     */
    public void setPatchPath(String patchPath) {
        this.patchPath = patchPath;
    }

    /**
     * @return Returns the driverClassName.
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * @param driverClassName
     *            The driverClassName to set.
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * @return Returns the database URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            The database URL to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return Returns the database username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            The database username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Returns the database password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            The database password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * retrieve the patch level
     */
    private int dbPatchLevel = -1;
    public int getDBPatchLevel(final String moduleName) {
        if ( dbPatchLevel < 0 ) {
            DataSourceMigrationContext context = initPatchContext(moduleName);
            if ( context == null ) {
                return dbPatchLevel;
            }
            JdbcMigrationLauncher launcher = new JdbcMigrationLauncher(context);
            launcher.setPatchPath(getPatchPath());
            try {
                dbPatchLevel = launcher.getDatabasePatchLevel(context);
            } catch (MigrationException e) {
                log.debug(e);
            }
        }
        return dbPatchLevel;
    }

    private DataSourceMigrationContext initPatchContext(final String moduleName) {

        AbstractDataSourceFactory dsFactory = new DBCPDataSourceFactory();
        try {
            dsFactory.setDriverClassName(getDriverClassName());
        } catch (Exception err) {
            log.debug(err);
            return null;
        }

        if (getUrl().indexOf('?') == -1) {
            dsFactory.setUrl(getUrl() + "?allowMultiQueries=true");
        } else {
            dsFactory.setUrl(getUrl() + "&allowMultiQueries=true");
        }

        dsFactory.setUsername(getUsername());
        dsFactory.setPassword(getPassword());
        dsFactory.setDefaultAutoCommit(false);

        DataSourceMigrationContext context = new DataSourceMigrationContext();
        context.setSystemName(moduleName);
        context.setDatabaseType(new DatabaseType(databaseType));
        context.setDataSource(dsFactory.createDataSource());

        return context;
    }
    
    @SuppressWarnings("unchecked")
    private void patchModule(final String patchPath, final String moduleName) {
        DataSourceMigrationContext context = initPatchContext(moduleName);
        if ( context == null ) {
            return;
        }
        
        log.info("Applying patches...");
        JdbcMigrationLauncher launcher = new JdbcMigrationLauncher(context);
        launcher.setPatchPath(patchPath);

        try {
            int prePatchLevel = launcher.getDatabasePatchLevel(context);

            RollbackableMigrationTask latestPatch = null;
            List<RollbackableMigrationTask> tasks = launcher.getMigrationProcess().getMigrationTasks();
            for(RollbackableMigrationTask patch : tasks) {
                if ( latestPatch == null ) {
                    latestPatch = patch;
                }
                else if ( patch.getLevel() > latestPatch.getLevel() ) {
                    latestPatch = patch;
                }
            }

            if ( latestPatch != null && latestPatch.getLevel() < prePatchLevel ) {
               throw new MigrationException("Your database(patchlevel=" +  prePatchLevel + ") seems to be more up-to-date than the latest available patch from your package: " + latestPatch + ". Check your package please!");
            }

            int patchesApplied = launcher.doMigrations();

            log.info("Applied " + patchesApplied + " " + (patchesApplied == 1 ? "patch" : "patches") + ".");

            dbPatchLevel = launcher.getDatabasePatchLevel(context);

        } catch (MigrationException e) {
            throw new RuntimeException("Error migrating patches: " + e, e);
        }
    }
    
    public void start() {
        // The names of the modules when there are more than one sub-directory of patches or a blank string when one
        final String patcherModuleNames = getModuleNames();
        final String trimmedPatcherModuleNames = (null == patcherModuleNames) ? null : patcherModuleNames.trim();
        final String[] modules = ((null == patcherModuleNames) || 0 == trimmedPatcherModuleNames.length()) 
            ? null 
            : trimmedPatcherModuleNames.split(",");
        if ((null == modules) || (0 == modules.length)) {
            patchModule(getPatchPath(), getSystemName());
        } else {
            final String rootPatchPath = getPatchPath();
            for (final String module: modules) {
                final String modulePatchPath = rootPatchPath.endsWith("/") 
                    ? rootPatchPath + module 
                    : rootPatchPath + '/' + module;
                patchModule(modulePatchPath, module);
            }
        }
    }

}
