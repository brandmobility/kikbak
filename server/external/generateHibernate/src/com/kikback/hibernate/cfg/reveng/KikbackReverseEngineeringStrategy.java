package com.tango.hibernate.cfg.reveng;

import java.util.Map;
import java.util.HashMap;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.MetaAttribute;

public class TangoReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

  public TangoReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
    super(delegate);
  }

  public Map tableToMetaAttributes(TableIdentifier identifier) {
    Map<String, MetaAttribute> metaAttributes = new HashMap<String, MetaAttribute>();

    // Add signature
    MetaAttribute classDesc = new MetaAttribute("class-description");
    classDesc.addValue("Generated using TangoReverseEngineeringStrategy");
    metaAttributes.put("class-description", classDesc);

    return metaAttributes;
  }

  public Map columnToMetaAttributes(TableIdentifier identifier, String column) {
    Map<String, MetaAttribute> metaAttributes = new HashMap<String, MetaAttribute>();

    // Generate the use-tostring meta attribute
    MetaAttribute tostr = new MetaAttribute("use-in-tostring");
    tostr.addValue("true");
    metaAttributes.put("use-in-tostring", tostr);

    return metaAttributes;
  }

}

