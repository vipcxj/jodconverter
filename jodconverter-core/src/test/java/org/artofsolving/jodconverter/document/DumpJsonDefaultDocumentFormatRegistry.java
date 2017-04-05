//
// JODConverter - Java OpenDocument Converter
// Copyright 2009 Art of Solving Ltd
// Copyright 2004-2009 Mirko Nasato
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package org.artofsolving.jodconverter.document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.List;

import org.artofsolving.jodconverter.ReflectionUtils;

/**
 * Exectable class that dumps a JSON version of the {@link DefaultDocumentFormatRegistry}
 */
class DumpJsonDefaultDocumentFormatRegistry {

    public static void main(String[] args) throws Exception {
        DefaultDocumentFormatRegistry registry = new DefaultDocumentFormatRegistry();
        List<DocumentFormat> formats = (List<DocumentFormat>) ReflectionUtils.getPrivateField(SimpleDocumentFormatRegistry.class, registry, "documentFormats");
        System.out.println(JSON.toJSONString(formats, SerializerFeature.PrettyFormat));
    }

}
