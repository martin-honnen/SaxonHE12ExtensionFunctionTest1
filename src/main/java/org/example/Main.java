package org.example;

import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws SaxonApiException {
        Processor processor = new Processor(false);

        processor.registerExtensionFunction(new ExtensionFunction() {
            @Override
            public QName getName() {
                return new QName("http://example.math.co.uk/demo", "sqrtSimple");
            }

            @Override
            public SequenceType[] getArgumentTypes() {
                return new SequenceType[]
                        {
                                SequenceType.makeSequenceType(ItemType.DOUBLE, OccurrenceIndicator.ZERO_OR_ONE)
                        };
            }

            @Override
            public SequenceType getResultType() {
                return SequenceType.makeSequenceType(ItemType.DOUBLE, OccurrenceIndicator.ZERO_OR_ONE);
            }

            @Override
            public XdmValue call(XdmValue[] xdmValues) throws SaxonApiException {
                return xdmValues[0].isEmpty() ? XdmEmptySequence.getInstance() : new XdmAtomicValue(Math.sqrt(((XdmAtomicValue) xdmValues[0]).getDoubleValue()));
            }
        });

        String xslt = "<xsl:transform version='3.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'" +
                "    xmlns:math='http://example.math.co.uk/demo'> " +
                " <xsl:template name='xsl:initial-template'> " +
                "   <out sqrt2='{math:sqrtSimple(2.0e0)}' " +
                "        sqrtEmpty='{math:sqrtSimple(())}'/> " +
                " </xsl:template>" +
                " </xsl:transform>";

        Xslt30Transformer xslt30Transformer = processor.newXsltCompiler().compile(new StreamSource(new StringReader(xslt))).load30();

        xslt30Transformer.callTemplate(null, processor.newSerializer(System.out));

    }

}