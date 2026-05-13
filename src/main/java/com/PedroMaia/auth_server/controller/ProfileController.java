package com.PedroMaia.auth_server.controller;


import com.PedroMaia.auth_server.dto.AuthResponseDTO;
import com.PedroMaia.auth_server.dto.UserResponseDTO;
import com.PedroMaia.auth_server.service.AuthService;
import com.PedroMaia.auth_server.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final AuthService authService;

    public ProfileController(ProfileService profileService, AuthService authService) {
        this.profileService = profileService;
        this.authService = authService;
    }

    @GetMapping()
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
        AuthResponseDTO authResponseDTO = authService.getProfile(authentication.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.cookie())
                .body(new UserResponseDTO(authResponseDTO.id(), authResponseDTO.name(), authResponseDTO.email(), authResponseDTO.role()));
    }

    @PatchMapping("/name")
    public ResponseEntity<UserResponseDTO> updateProfileName(@RequestBody String name, Authentication authentication) {
        UserResponseDTO userResponseDTO = profileService.updateProfileName(name, authentication.getName());

        return ResponseEntity.ok()
                .body(userResponseDTO);
    }

    @PatchMapping("/password")
    public ResponseEntity<UserResponseDTO> updateProfilePassword(@RequestBody String oldPassword, @RequestBody String newPassword, Authentication authentication) {
         UserResponseDTO userResponseDTO = profileService.updateProfilePassword(oldPassword, newPassword, authentication.getName());

         return ResponseEntity.ok()
                 .body(userResponseDTO);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteProfile(@RequestBody String password, Authentication authentication) {
        profileService.deleteProfile(password, authentication.getName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
