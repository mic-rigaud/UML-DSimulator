<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xmi="http://www.omg.org/spec/XMI/20131001" xmlns:uml="http://www.eclipse.org/uml2/5.0.0/UML" xmlns:scxml="http://www.w3.org/2005/07/scxml">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" />


  <xsl:template match="/">
    <xsl:variable name="filename" select="test" />
    <diagram author="Michael" name="test">
      <description>
        test uml model
      </description>
      <xsl:apply-templates/>
    </diagram>
  </xsl:template>


  <xsl:template match="packagedElement">
    <xsl:variable name="type" select="@xmi:type" />
    <xsl:variable name="name" select="@name" />
    <xsl:variable name="id" select="@xmi:id" />
    <xsl:if test="$type='uml:Class'">
      <xsl:element name="class">
        <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
        <xsl:choose>
          <xsl:when test="$name='SUS'">
            <xsl:attribute name="default">true</xsl:attribute>
            <xsl:element name="relationships">
              <xsl:for-each select="ownedAttribute">
                <xsl:call-template name="SUS_relationships"/>
              </xsl:for-each>
            </xsl:element>
            <xsl:call-template name="SUS_StateMachine"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="class_constructor"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
    </xsl:if>
  </xsl:template>


  <xsl:template name="SUS_relationships">
    <xsl:variable name="type" select="@type"/>
    <xsl:variable name="name" select="/uml:Model/packagedElement[@name!='SUS']/ownedAttribute[@type=$type]/@name"/>
    <xsl:variable name="class" select="/uml:Model/packagedElement[@xmi:id=$type]/@name"/>
    <xsl:variable name="low_value" select="/uml:Model/packagedElement[@xmi:id=$type]/ownedAttribute/lowerValue/@value"/>
    <xsl:variable name="upper_value" select="/uml:Model/packagedElement[@xmi:id=$type]/ownedAttribute/upperValue/@value"/>
    <xsl:element name="association">
      <xsl:choose>
        <xsl:when test="empty($name)">
          <xsl:attribute name="name">class_<xsl:value-of select="$class"/></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:attribute name="class"><xsl:value-of select="$class"/></xsl:attribute>
      <xsl:if test="not(empty($low_value))">
        <xsl:attribute name="min"><xsl:value-of select="$low_value"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="not(empty($upper_value))">
        <xsl:attribute name="max"><xsl:value-of select="$upper_value"/></xsl:attribute>
      </xsl:if>
    </xsl:element>
  </xsl:template>

  <xsl:template name="SUS_StateMachine">
    <xsl:variable name="max" select="count(/uml:Model/packagedElement[@name='SUS']/ownedAttribute)"/>
    <xsl:element name="constructor">
      <xsl:element name="body">
        self.source = None
        self.state_variables = {}
        self.state_variables["counter"]=0
      </xsl:element>
    </xsl:element>
    <xsl:element name="scxml">
      <xsl:attribute name="initial">state1</xsl:attribute>
      <xsl:for-each select="ownedAttribute">
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="expr" select="/uml:Model/packagedElement[@name!='SUS']/ownedAttribute[@type=$type]/@name"/>
        <xsl:variable name="class" select="/uml:Model/packagedElement[@xmi:id=$type]/@name"/>
        <state id="state{position()}">
          <onentry>
            <raise scope="cd" event="create_instance">
              <xsl:choose>
                <xsl:when test="empty($expr)">
                  <parameter expr="'class_{$class}'"/>
                </xsl:when>
                <xsl:otherwise>
                  <parameter expr="'{$expr}'"/>
                </xsl:otherwise>
              </xsl:choose>
              <parameter expr="'{$class}'"/>
            </raise>
          </onentry>
          <transition event="instance_created" target="../state{position()+1}">
            <parameter name ="association_name" type ="string"/>
            <raise scope ="cd" event ="start_instance">
              <parameter expr ="association_name" />
            </raise>
          </transition>
        </state>
      </xsl:for-each>
      <state id="state{$max+1}">
      </state>
    </xsl:element>
  </xsl:template>







  <xsl:template name="class_constructor">
    <xsl:variable name="name" select="@name"/>
    <xsl:element name="relationships">
      <xsl:element name="association">
        <xsl:attribute name="name">parent</xsl:attribute>
        <xsl:attribute name="class">SUS</xsl:attribute>
      </xsl:element>
    </xsl:element>
    <xsl:element name="constructor">
      <xsl:element name="body">
        self.source = None
        <xsl:for-each select="ownedAttribute">
          <xsl:variable name="type_id" select="@type"/>
          <xsl:if test="/uml:Model/packagedElement/packagedElement[@xmi:id=$type_id]/@xmi:type='uml:PrimitiveType'">
        self.<xsl:value-of select="@name"/>=False
            <!-- A ameliorer!! -->
          </xsl:if>
        </xsl:for-each>
      </xsl:element>
    </xsl:element>
    <xsl:for-each select="ownedBehavior[@xmi:type='uml:StateMachine']">
      <xsl:call-template name="StateMachine"/>
    </xsl:for-each>
  </xsl:template>


  <xsl:template name="StateMachine">
    <xsl:variable name="name" select="@name"/>
    <xsl:element name="scxml">
      <xsl:attribute name="initial"><xsl:value-of select="region/subvertex[@xmi:type='uml:Pseudostate'][empty(@kind)]/@name"/></xsl:attribute>
      <xsl:apply-templates select="region/subvertex" />
    </xsl:element>
  </xsl:template>



  <xsl:template match="subvertex">
    <xsl:variable name="type" select="@xmi:type" />
    <xsl:variable name="xmiid" select="@xmi:id" />
    <xsl:variable name="name" select="@name" />
    <xsl:variable name="id" select="if (empty($name)) then $xmiid else $name" />
    <xsl:choose>
      <xsl:when test="$type='uml:Pseudostate'">
        <xsl:choose>
          <xsl:when test="@kind='deepHistory'">
            <history type="deep" id="{$id}">
              <xsl:apply-templates select="../transition[@source=$xmiid]" />
            </history>
          </xsl:when>
          <xsl:when test="@kind='shallowHistory'">
            <history type="shallow" id="{$id}">
              <xsl:apply-templates select="../transition[@source=$xmiid]" />
            </history>
          </xsl:when>
          <xsl:otherwise>
            <state id="{$id}">
              <xsl:apply-templates select="../transition[@source=$xmiid]" />
            </state>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$type='uml:State'">
	<state id="{$id}">
	  <onentry>
            <script>
              print("etat: <xsl:value-of select="$id"/>")
            </script>
          </onentry>
          <xsl:apply-templates select="../transition[@source=$xmiid]" />
        </state>
      </xsl:when>
      <xsl:when test="$type='uml:FinalState'">
        <final id="{$id}" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="transition">
    <xsl:variable name="target" select="@target" />
    <xsl:variable name="tname" select="if (empty(../subvertex[@xmi:id=$target]/@name)) then ../subvertex[@xmi:id=$target]/@xmi:id else ../subvertex[@xmi:id=$target]/@name" />
    <xsl:element name="transition">
      <xsl:attribute name="target">../<xsl:value-of select="$tname"/></xsl:attribute>
      <xsl:for-each select="trigger">
        <xsl:call-template name="trigger"/>
      </xsl:for-each>
      <xsl:choose>
        <xsl:when test="not(empty(effect/@xmi:id))">
          <script>
            print("<xsl:value-of select="effect/body"/>")
            <xsl:if test="contains(effect/body,':=') and contains(effect/language,'ABCD')">
              <xsl:variable name="name" select="substring-before(effect/body,' :=')"/>
              <xsl:variable name="value" select="replace(replace(substring-before(substring-after(effect/body,':= '),';'),'false','False'),'true','True')"/>
            self.<xsl:value-of select="$name"/>=<xsl:value-of select="replace($value,$name,concat('self.',$name))"/>
            </xsl:if>

          </script>
          <xsl:if test="contains(effect/body,'send') and contains(effect/language,'ABCD')">
            <xsl:variable name="event_name" select="substring-before(substring-after(effect/body,'send '),' to')"/>
            <xsl:variable name="uri" select="substring-before(substring-after(effect/body,'to '),';')"/>
            <raise event="{$event_name}" scope="narrow" target="'parent/{$uri}'"/>
          </xsl:if>
        </xsl:when>
      </xsl:choose>
    </xsl:element>
  </xsl:template>



  <xsl:template name="trigger">
    <xsl:variable name="event" select="@event" />
    <xsl:variable name="id" select="@xmi:id" />
    <xsl:variable name="signal" select="/uml:Model/packagedElement[@xmi:id=$event]/@signal" />
    <xsl:variable name="event_name" select="/uml:Model/packagedElement[@xmi:id=$signal]/@name" />
    <xsl:attribute name="event"><xsl:value-of select="$event_name"/></xsl:attribute>
    <xsl:if test="not(empty(../ownedRule/@xmi:id)) and ../ownedRule/specification/language='ABCD'">
      <xsl:variable name="condition" select="concat('self.', ../ownedRule/specification/body)"/>
      <xsl:attribute name="cond"><xsl:value-of select="$condition"/></xsl:attribute>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
