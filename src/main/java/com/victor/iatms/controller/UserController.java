package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.AssignUserProjectDTO;
import com.victor.iatms.entity.dto.CreateUserDTO;
import com.victor.iatms.entity.dto.UpdateUserDTO;
import com.victor.iatms.entity.dto.UpdateUserStatusDTO;
import com.victor.iatms.entity.dto.UserProjectItemDTO;
import com.victor.iatms.entity.dto.UserProjectsQueryDTO;
import com.victor.iatms.entity.dto.UserQueryDTO;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 3.1. 分页查询用户列表
     */
    @GetMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<PaginationResultVO<User>> getUserList(UserQueryDTO userQueryDTO) {
        PaginationResultVO<User> userList = userService.findUserListByPage(userQueryDTO);
        return ResponseVO.success("success", userList);
    }

    /**
     * 3.2. 添加用户
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true, checkAdmin = true)
    public ResponseVO<Object> addUser(@RequestBody CreateUserDTO createUserDTO, HttpServletRequest request) {
        Integer creatorId = (Integer) request.getAttribute("userId");
        Integer newUserId = userService.addUser(createUserDTO, creatorId);
        return ResponseVO.success("用户创建成功", newUserId);
    }

    /**
     * 3.3. 更新用户信息
     */
    @PutMapping("/{userId}")
    @GlobalInterceptor(checkLogin = true, checkAdmin = true)
    public ResponseVO<Object> updateUser(@PathVariable("userId") Integer userId, @RequestBody UpdateUserDTO updateUserDTO) {
        userService.updateUser(userId, updateUserDTO);
        return ResponseVO.success("用户信息更新成功", null);
    }

    /**
     * 3.4. 更新用户状态
     */
    @PutMapping("/{userId}/status")
    @GlobalInterceptor(checkLogin = true, checkAdmin = true)
    public ResponseVO<Object> updateUserStatus(@PathVariable("userId") Integer userId, @RequestBody UpdateUserStatusDTO updateUserStatusDTO) {
        userService.updateUserStatus(userId, updateUserStatusDTO);
        return ResponseVO.success("用户状态已更新", null);
    }

    /**
     * 3.5. 删除用户
     */
    @DeleteMapping("/{userId}")
    @GlobalInterceptor(checkLogin = true, checkAdmin = true)
    public ResponseVO<Object> deleteUser(@PathVariable("userId") Integer userId, HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("userId");
        userService.deleteUser(userId, currentUserId);
        return ResponseVO.success("用户删除成功", null);
    }

    /**
     * 3.6 为用户分配项目
     */
    @PostMapping("/{userId}/projects")
    @GlobalInterceptor(checkLogin = true, checkAdmin = true)
    public ResponseVO<Object> assignUserToProject(@PathVariable("userId") Integer userId,
                                                  @RequestBody AssignUserProjectDTO dto,
                                                  HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        Integer memberId = userService.assignUserToProject(userId, dto, operatorId);
        return ResponseVO.success("用户已成功分配到项目", java.util.Collections.singletonMap("member_id", memberId));
    }

    /**
     * 3.7 移除用户项目分配（软删除）
     */
    @DeleteMapping("/{userId}/projects/{projectId}")
    @GlobalInterceptor(checkLogin = true, checkAdmin = true)
    public ResponseVO<Object> removeUserFromProject(@PathVariable("userId") Integer userId,
                                                    @PathVariable("projectId") Integer projectId,
                                                    HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        userService.removeUserFromProject(userId, projectId, operatorId);
        return ResponseVO.success("用户已从项目中成功移除", null);
    }

    /**
     * 3.8 分页获取用户项目列表
     */
    @GetMapping("/{userId}/projects")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<PaginationResultVO<UserProjectItemDTO>> getUserProjects(@PathVariable("userId") Integer userId,
                                                                              @RequestParam(value = "status", required = false) String status,
                                                                              @RequestParam(value = "project_role", required = false) String projectRole,
                                                                              @RequestParam(value = "page", required = false) Integer page,
                                                                              @RequestParam(value = "page_size", required = false) Integer pageSize) {
        UserProjectsQueryDTO query = new UserProjectsQueryDTO();
        query.setUserId(userId);
        query.setStatus(status);
        query.setProjectRole(projectRole);
        query.setPage(page);
        query.setPageSize(pageSize);
        PaginationResultVO<UserProjectItemDTO> result = userService.findUserProjects(query);
        return ResponseVO.success("success", result);
    }
}
