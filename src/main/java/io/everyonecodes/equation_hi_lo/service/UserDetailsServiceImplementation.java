package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.Player;
import io.everyonecodes.equation_hi_lo.repository.PlayerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

//Spring Security’s “bridge” between the database and the security framework

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public UserDetailsServiceImplementation(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  // called by Spring Security when user tries to log in

        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        //Convert player roles into Spring Security authorities
        Set<GrantedAuthority> authorities = Arrays.stream(player.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new User(player.getUsername(), player.getPassword(), authorities);
    }
}