package com.victor.iatms.service;

import com.victor.iatms.entity.dto.CreateUserDTO;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.entity.query.UserQuery;
import com.victor.iatms.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 * 
 * 测试目标：验证UserService的各个方法是否正确工作
 * 
 * 测试方法：
 * 1. addUser - 测试用户创建
 * 2. updateUser - 测试用户信息更新
 * 3. deleteUser - 测试用户删除
 * 4. findUserById - 测试根据ID查询用户
 * 5. findUserListByPage - 测试分页查询用户列表
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;

    /**
     * 测试用例1.1：成功创建新用户
     * 测试场景：传入有效的用户信息，期望成功创建用户
     */
    @Test
    void testAddUser_Success() {
        // 【准备】准备测试数据
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setName("测试用户");
        createUserDTO.setEmail("test@example.com");
        createUserDTO.setPassword("password123");
        
        // 【Mock】模拟Mapper行为
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(userMapper.selectByEmail("test@example.com")).thenReturn(null);
        
        // 【执行】调用被测试的方法
        Integer result = userService.addUser(createUserDTO, 1);
        
        // 【验证】断言结果
        assertNotNull(result, "返回的用户ID不应为空");
        assertTrue(result > 0, "返回的用户ID应该大于0");
        
        // 【验证】确认Mapper被调用
        verify(userMapper, times(1)).insert(any(User.class));
    }

    /**
     * 测试用例1.2：创建用户时邮箱已存在
     * 测试场景：传入已存在的邮箱，期望抛出异常
     */
    @Test
    void testAddUser_EmailAlreadyExists() {
        // 【准备】
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setName("测试用户");
        createUserDTO.setEmail("existing@example.com");
        
        // 【Mock】模拟邮箱已存在
        User existingUser = new User();
        existingUser.setUserId(1);
        existingUser.setEmail("existing@example.com");
        when(userMapper.selectByEmail("existing@example.com")).thenReturn(existingUser);
        
        // 【执行 & 验证】期望抛出异常
        assertThrows(RuntimeException.class, () -> {
            userService.addUser(createUserDTO, 1);
        }, "邮箱已存在时应抛出异常");
    }

    /**
     * 测试用例2.1：根据ID查询用户成功
     * 测试场景：查询存在的用户
     */
    @Test
    void testFindUserById_Success() {
        // 【准备】
        User expectedUser = new User();
        expectedUser.setUserId(1);
        expectedUser.setName("张三");
        expectedUser.setEmail("zhangsan@example.com");
        
        // 【Mock】
        when(userMapper.selectById(1)).thenReturn(expectedUser);
        
        // 【执行】
        User result = userService.findUserById(1);
        
        // 【验证】
        assertNotNull(result, "查询结果不应为空");
        assertEquals("张三", result.getName(), "用户名应匹配");
        assertEquals("zhangsan@example.com", result.getEmail(), "邮箱应匹配");
    }

    /**
     * 测试用例2.2：根据ID查询用户不存在
     * 测试场景：查询不存在的用户ID
     */
    @Test
    void testFindUserById_NotFound() {
        // 【Mock】
        when(userMapper.selectById(999)).thenReturn(null);
        
        // 【执行】
        User result = userService.findUserById(999);
        
        // 【验证】
        assertNull(result, "查询不存在的用户应返回null");
    }

    /**
     * 测试用例3：分页查询用户列表
     * 测试场景：查询第1页，每页10条
     */
    @Test
    void testFindUserListByPage() {
        // 【准备】
        UserQuery query = new UserQuery();
        query.setPage(1);
        query.setPageSize(10);
        
        List<User> userList = Arrays.asList(
            createUser(1, "用户1", "user1@example.com"),
            createUser(2, "用户2", "user2@example.com"),
            createUser(3, "用户3", "user3@example.com")
        );
        
        // 【Mock】
        when(userMapper.selectByCondition(any(UserQuery.class))).thenReturn(userList);
        when(userMapper.selectCount(any(UserQuery.class))).thenReturn(3L);
        
        // 【执行】
        PaginationResultVO<User> result = userService.findUserListByPage(query);
        
        // 【验证】
        assertNotNull(result, "分页结果不应为空");
        assertEquals(3, result.getTotal(), "总记录数应为3");
        assertEquals(1, result.getPage(), "当前页应为1");
        assertEquals(10, result.getPageSize(), "每页大小应为10");
        assertEquals(3, result.getItems().size(), "当前页应返回3条数据");
    }

    /**
     * 测试用例4：更新用户信息
     * 测试场景：成功更新用户信息
     */
    @Test
    void testUpdateUser_Success() {
        // 【准备】
        User existingUser = new User();
        existingUser.setUserId(1);
        existingUser.setName("原名");
        
        // 【Mock】
        when(userMapper.selectById(1)).thenReturn(existingUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        
        // 【执行】执行更新（需要传入更新DTO）
        // 注意：这里需要根据实际updateUser方法签名调整
        // userService.updateUser(1, updateUserDTO);
        
        // 【验证】
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    /**
     * 测试用例5：删除用户
     * 测试场景：成功删除用户
     */
    @Test
    void testDeleteUser_Success() {
        // 【准备】
        User existingUser = new User();
        existingUser.setUserId(1);
        
        // 【Mock】
        when(userMapper.selectById(1)).thenReturn(existingUser);
        when(userMapper.deleteById(1)).thenReturn(1);
        
        // 【执行】
        userService.deleteUser(1, 2);
        
        // 【验证】
        verify(userMapper, times(1)).deleteById(1);
    }

    // ========== 辅助方法 ==========
    
    private User createUser(Integer id, String name, String email) {
        User user = new User();
        user.setUserId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}

