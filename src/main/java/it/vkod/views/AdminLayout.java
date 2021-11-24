package it.vkod.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.vkod.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.concurrent.atomic.AtomicLong;

@PageTitle( "System Admin - Full Access" )
@Route( "adm" )
@RolesAllowed( { "ADMIN", "MANAGER", "LEADER" } )
public class AdminLayout extends VerticalLayout {

	private final AdminService adminService;


	public AdminLayout( @Autowired AdminService adminService ) {

		this.adminService = adminService;

		final AtomicLong deleteCounter = new AtomicLong();

		final var flushButton = new Button( "Flush Database", onClick -> {
			long deletedCount = adminService.flushDatabase();
			deleteCounter.set( deletedCount );
		} );

		add( flushButton );

	}

}
