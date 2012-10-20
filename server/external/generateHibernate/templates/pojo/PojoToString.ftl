<#if pojo.needsToString()>    /**
     * toDtoString
     * @return String
     *
     *  Appends and returns toString() of each field in this object.  toDtoString() does not call toDtoString()
     *  of this class's members, this avoids infinite recursion with bi-directionally referenced objects.
     */
     public String toDtoString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
<#foreach property in pojo.getToStringPropertiesIterator()>      buffer.append("${property.getName()}").append("='").append(${pojo.getGetterSignature(property)}()).append("' ");			
</#foreach>      buffer.append("]");
      
      return buffer.toString();
     }
</#if>
