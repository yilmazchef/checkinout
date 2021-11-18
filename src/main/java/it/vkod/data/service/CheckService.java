package it.vkod.data.service;


import it.vkod.data.entity.Check;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
@RequiredArgsConstructor( access = AccessLevel.PUBLIC )
@Getter
public class CheckService extends CrudService< Check, Long > {

	private final CheckRepository repository;


	@Transactional
	public List< Check > findByUser( final String email ) {

		return findByUser( email, 1, 100 );
	}


	@Transactional
	public List< Check > findByUser( final String email, int pageNo, int pageSize ) {

		return repository.findAllByUser_Username( email, PageRequest.of( pageNo, pageSize ) ).toList();
	}


	@Transactional
	public Long save( final Check check ) {

		return repository.save( check ).getId();
	}

}
