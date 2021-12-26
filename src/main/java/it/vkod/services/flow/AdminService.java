package it.vkod.services.flow;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {


	public AdminService( @Autowired AuthenticationService authenticationService ) {

	}

}
