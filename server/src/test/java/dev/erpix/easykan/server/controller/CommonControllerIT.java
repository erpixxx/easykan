package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.domain.token.security.JwtProvider;
import dev.erpix.easykan.server.domain.user.service.JpaUserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class CommonControllerIT {

	@SuppressWarnings("unused")
	@MockitoBean
	private JpaUserDetailsService jpaUserDetailsService;

	@SuppressWarnings("unused")
	@MockitoBean
	private JwtProvider jwtProvider;

}
