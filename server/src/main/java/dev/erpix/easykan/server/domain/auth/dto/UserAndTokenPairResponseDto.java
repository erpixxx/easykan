package dev.erpix.easykan.server.domain.auth.dto;

import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.user.dto.UserResponseDto;

public record UserAndTokenPairResponseDto(UserResponseDto user,TokenPairDto tokenPair){}
