package org.example.langchain.customer;

import lombok.RequiredArgsConstructor;
import org.example.langchain.config.JwtTokenProvider;
import org.example.langchain.customer.dto.LoginRequest;
import org.example.langchain.customer.dto.LoginResponse;
import org.example.langchain.customer.dto.UserDto;
import org.example.langchain.review.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.example.langchain.customer.dto.ChangePasswordRequest;
import org.example.langchain.customer.dto.UpdateUserRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        if (userRepository.findByName(userDto.getName()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        // 创建新用户
        User user = new User();
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setBirthday(userDto.getBirthday());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getName(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken((User) authentication.getPrincipal());

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new LoginResponse(jwt, user.getId(), user.getName()));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    // 在UserController类中添加以下方法
    
    /**
     * 修改用户密码
     */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        User currentUser = (User) authentication.getPrincipal();
        
        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "当前密码错误"));
        }
        
        // 更新密码
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        
        return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/user")
    public ResponseEntity<ApiResponse<User>> updateUser(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {
        User currentUser = (User) authentication.getPrincipal();
        
        // 更新用户信息
        if (request.getUsername() != null) {
            currentUser.setName(request.getUsername());
        }
        if (request.getEmail() != null) {
            // 注意：需要在User实体中添加email字段
            // currentUser.setEmail(request.getEmail());
        }
        if (request.getBirthday() != null) {
            currentUser.setBirthday(request.getBirthday());
        }
        
        User updatedUser = userRepository.save(currentUser);
        
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", updatedUser));
    }
}