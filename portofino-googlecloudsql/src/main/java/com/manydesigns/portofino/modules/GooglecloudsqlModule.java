/*
 * Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.manydesigns.portofino.modules;

import com.manydesigns.portofino.database.platforms.DatabasePlatformsManager;
import com.manydesigns.portofino.database.platforms.GoogleCloudSQLDatabasePlatform;
import com.manydesigns.portofino.di.Inject;
import com.manydesigns.portofino.liquibase.databases.GoogleCloudSQLDatabase;
import com.manydesigns.portofino.liquibase.sqlgenerators.GoogleCloudSQLLockDatabaseChangeLogGenerator;
import liquibase.database.DatabaseFactory;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class GooglecloudsqlModule implements Module {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    @Inject(BaseModule.PORTOFINO_CONFIGURATION)
    public Configuration configuration;

    @Inject(DatabaseModule.DATABASE_PLATFORMS_MANAGER)
    DatabasePlatformsManager databasePlatformsManager;

    protected ModuleStatus status = ModuleStatus.CREATED;

    //**************************************************************************
    // Logging
    //**************************************************************************

    public static final Logger logger =
            LoggerFactory.getLogger(GooglecloudsqlModule.class);

    @Override
    public String getModuleVersion() {
        return ModuleRegistry.getPortofinoVersion();
    }

    @Override
    public int getMigrationVersion() {
        return 1;
    }

    @Override
    public double getPriority() {
        return 3;
    }

    @Override
    public String getId() {
        return "googlecloudsql";
    }

    @Override
    public String getName() {
        return "Google Cloud SQL";
    }

    @Override
    public int install() {
        return 1;
    }

    @Override
    public void init() {
        logger.debug("Registering Google Cloud SQL");
        DatabaseFactory.getInstance().register(new GoogleCloudSQLDatabase());
        logger.debug("Registering GoogleCloudSQLLockDatabaseChangeLogGenerator");
        SqlGeneratorFactory.getInstance().register(
                new GoogleCloudSQLLockDatabaseChangeLogGenerator());
        databasePlatformsManager.addDatabasePlatform(new GoogleCloudSQLDatabasePlatform());
        status = ModuleStatus.ACTIVE;
    }
@Override
    public void destroy() {
        status = ModuleStatus.DESTROYED;
    }

    @Override
    public ModuleStatus getStatus() {
        return status;
    }
}