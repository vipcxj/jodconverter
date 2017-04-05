/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.artofsolving.jodconverter.office;

import java.util.Properties;
import org.artofsolving.jodconverter.util.PropertiesConfiguration;

/**
 *
 * @author Administrator
 */
public class AbstractOfficeManagerConfiguration {

    protected final PropertiesConfiguration propertiesUtils;

    public AbstractOfficeManagerConfiguration() {
        this.propertiesUtils = new PropertiesConfiguration();
    }

    public AbstractOfficeManagerConfiguration load(Properties properties, String prefix) {
        propertiesUtils.load(properties, prefix);
        return this;
    }
}
