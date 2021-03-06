/*
 * */
package com.synectiks.process.server.migrations;

import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.migrations.V20180214093600_AdjustDashboardPositionToNewResolution.Migration;
import com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations.GrantsMetaMigration;
import com.synectiks.process.server.plugin.PluginModule;

public class MigrationsModule extends PluginModule {
    @Override
    protected void configure() {
        addMigration(V20151210140600_ElasticsearchConfigMigration.class);
        addMigration(V20161116172100_DefaultIndexSetMigration.class);
        addMigration(V20161116172200_CreateDefaultStreamMigration.class);
        addMigration(V20161122174500_AssignIndexSetsToStreamsMigration.class);
        addMigration(V20161124104700_AddRetentionRotationAndDefaultFlagToIndexSetMigration.class);
        addMigration(V20161125142400_EmailAlarmCallbackMigration.class);
        addMigration(V20161125161400_AlertReceiversMigration.class);
        addMigration(V20161130141500_DefaultStreamRecalcIndexRanges.class);
        addMigration(V20161215163900_MoveIndexSetDefaultConfig.class);
        addMigration(V20161216123500_DefaultIndexSetMigration.class);
        addMigration(V20170110150100_FixAlertConditionsMigration.class);
        addMigration(V20170607164210_MigrateReopenedIndicesToAliases.class);
        addMigration(Migration.class);
        addMigration(V2018070614390000_EnforceUniqueGrokPatterns.class);
        addMigration(V20180718155800_AddContentPackIdAndRev.class);
        addMigration(V20180924111644_AddDefaultGrokPatterns.class);
        addMigration(V20190705071400_AddEventIndexSetsMigration.class);
        addMigration(V20190730100900_AddAlertsManagerRole.class);
        addMigration(V20190730000000_CreateDefaultEventsConfiguration.class);
        addMigration(V20191121145100_FixDefaultGrokPatterns.class);
        addMigration(V20191129134600_CreateInitialUrlWhitelist.class);
//        addMigration(V20191219090834_AddSourcesPage.class);
        addMigration(V20200102140000_UnifyEventSeriesId.class);
        addMigration(V20200226181600_EncryptAccessTokensMigration.class);
        addMigration(V20200722110800_AddBuiltinRoles.class);
        addMigration(GrantsMetaMigration.class);
        addMigration(V20201103145400_LegacyAuthServiceMigration.class);

        // Make sure there is always a binder for migration modules
        Multibinder.newSetBinder(binder(), V20201103145400_LegacyAuthServiceMigration.MigrationModule.class);
    }
}
