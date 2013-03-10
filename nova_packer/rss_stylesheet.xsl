<xsl:stylesheet
        version="2.0"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="channel">

        <xsl:text>Feed name:&#xA;</xsl:text>
        <h1>
            <xsl:value-of select="title"/>
        </h1>

        <xsl:for-each select="item">
            <h3>
                <xsl:value-of select="title"/>
            </h3>
            <p>
                <xsl:value-of select="description"/>
            </p>
            <p>
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="link"/>
                    </xsl:attribute>
                    <xsl:value-of select="link"/>
                </a>
            </p>
            <hr/>
        </xsl:for-each>

    </xsl:template>


</xsl:stylesheet>
