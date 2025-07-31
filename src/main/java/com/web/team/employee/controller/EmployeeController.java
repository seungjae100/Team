package com.web.team.employee.controller;

import com.web.team.employee.service.EmployeeService;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "accessToken")
@RequiredArgsConstructor
@Tag(name = "직원 계정 API", description = "관리자가 직원을 관리하는 API 입니다.")
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "관리자의 직원 등록", description = "관리자가 직원의 등록을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 등록 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/register")
    public ResponseEntity<String> userRegister(@RequestBody UserRegisterRequest request) {
        employeeService.registerEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("직원 등록이 완료되었습니다.");
    }

    @Operation(summary = "관리자의 직원 수정", description = "관리자가 직원의 수정을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 수정 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PatchMapping("/update")
    public ResponseEntity<String> userUpdate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserUpdateRequest request) {
        employeeService.updateEmployee(userDetails.getUserId(), request);
        return ResponseEntity.ok("정보 수정 완료");
    }

    @Operation(summary = "직원 목록 조회", description = "관리자는 전체, 일반 직원은 활성화된 직원만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 목록 조회 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @GetMapping
    public ResponseEntity<?> getAllEmployees(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(employeeService.getAllEmployees(userDetails));
    }

    @Operation(summary = "직원 개인 조회", description = "관리자는 전체, 일반 직원은 활성화된 직원만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 정보 조회 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/response/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/response/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/response/InternalServerError")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id, userDetails));
    }

}
