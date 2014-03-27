<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:strip-space elements="*"/>
<xsl:output indent="yes" encoding="utf-8"/>
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
<xsl:template match="GlobalNamingResources">
  <GlobalNamingResources>
     <xsl:apply-templates select="@* | *"/>
     <Resource
     	name="WebIDDatabase" auth="Container"
	type="com.turnguard.webid.tomcat.database.WebIDDatabase"
	description="Using virtuoso as roles sources"
	factory="com.turnguard.webid.tomcat.database.impl.virtuoso.VirtuosoWebIDDatabaseFactoryImpl"            
	roleGraph="http://webidrealm.localhost/rolegraph"
	host="localhost"
	port="1111"
	user="dba"
	pass="dba"/>
  </GlobalNamingResources>
</xsl:template>
<xsl:template match="Service">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <Connector
	port="8443"
	SSLImplementation="org.jsslutils.extra.apachetomcat6.JSSLutilsImplementation"
	acceptAnyCert="true"
	scheme="https"
	maxThreads="150"
	secure="true"
	SSLEnabled="true"
	sslProtocol="TLS"
	clientAuth="true"
	keystoreFile="/var/lib/tomcat7/conf/tomcat.keystore"
	keystorePass="password"
	/>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>
</xsl:stylesheet>
