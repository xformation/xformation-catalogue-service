package com.synectiks.process.server.xformation.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.synectiks.process.server.xformation.service.CollectorService;
import com.synectiks.process.server.xformation.service.CollectorServiceImpl;
import com.synectiks.process.server.xformation.service.DashboardService;
import com.synectiks.process.server.xformation.service.DashboardServiceImpl;
import com.synectiks.process.server.xformation.service.FolderService;
import com.synectiks.process.server.xformation.service.FolderServiceImpl;
import com.synectiks.process.server.xformation.service.LibraryService;
import com.synectiks.process.server.xformation.service.LibraryServiceImpl;
import com.synectiks.process.server.xformation.service.ManageViewService;
import com.synectiks.process.server.xformation.service.ManageViewServiceImpl;

public class PostGsJpaModule extends AbstractModule {
	private static final Logger LOG = LoggerFactory.getLogger(PostGsJpaModule.class);
			
	@Override
    protected void configure() {
        install(new JpaPersistModule("postGsPu"));
        bind(JPAInitializer.class).asEagerSingleton();
        bind(CollectorService.class).to(CollectorServiceImpl.class);
        bind(DashboardService.class).to(DashboardServiceImpl.class);
        bind(FolderService.class).to(FolderServiceImpl.class);
        bind(LibraryService.class).to(LibraryServiceImpl.class);
        bind(ManageViewService.class).to(ManageViewServiceImpl.class);
    }

    @Singleton
    private static class JPAInitializer {
        @Inject
        public JPAInitializer(final PersistService service) {
        	LOG.info("Starting JPA persist service");
            service.start();
        }
    }

}
