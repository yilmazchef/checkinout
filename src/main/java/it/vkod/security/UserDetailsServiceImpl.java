package it.vkod.security;


import it.vkod.data.entity.User;
import it.vkod.data.service.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;


	@Override
	public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {

		final var oUser = userRepository.findByUsername( username );
		if ( oUser.isEmpty() ) {
			throw new UsernameNotFoundException( "No user present with username: " + username );
		} else {
			return new org.springframework.security.core.userdetails.User( oUser.get().getUsername(), oUser.get().getHashedPassword(),
					getAuthorities( oUser.get() ) );
		}
	}


	private static List< GrantedAuthority > getAuthorities( User user ) {

		return user.getRoles().stream().map( role -> new SimpleGrantedAuthority( "ROLE_" + role.getTitle() ) )
				.collect( Collectors.toList() );

	}

}
