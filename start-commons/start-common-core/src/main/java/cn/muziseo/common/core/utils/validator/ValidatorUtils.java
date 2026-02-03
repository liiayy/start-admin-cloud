package cn.muziseo.common.core.utils.validator;

import cn.muziseo.common.core.utils.spring.SpringUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Validator 校验框架工具类
 * 提供便捷的参数校验方法
 *
 * @author 李彦军
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorUtils {

    private static final Validator VALIDATOR = SpringUtils.getBean(Validator.class);

    /* ------------------------- 基础校验方法 ------------------------- */

    /**
     * 校验单个对象（使用默认分组）
     *
     * @param object 要校验的对象
     * @param <T>    对象类型
     * @throws ConstraintViolationException 校验失败时抛出
     */
    public static <T> void validate(T object) {
        validate(object, Default.class);
    }

    /**
     * 校验单个对象（指定校验分组）
     *
     * @param object 要校验的对象
     * @param groups 校验分组
     * @param <T>    对象类型
     * @throws ConstraintViolationException 校验失败时抛出
     */
    public static <T> void validate(T object, Class<?>... groups) {
        if (object == null) {
            throw new ConstraintViolationException("校验对象不能为null", Collections.emptySet());
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("参数校验失败", violations);
        }
    }

    /**
     * 校验对象的指定属性（使用默认分组）
     *
     * @param object       要校验的对象
     * @param propertyName 属性名
     * @param <T>          对象类型
     * @throws ConstraintViolationException 校验失败时抛出
     */
    public static <T> void validateProperty(T object, String propertyName) {
        validateProperty(object, propertyName, Default.class);
    }

    /**
     * 校验对象的指定属性（指定校验分组）
     *
     * @param object       要校验的对象
     * @param propertyName 属性名
     * @param groups       校验分组
     * @param <T>          对象类型
     * @throws ConstraintViolationException 校验失败时抛出
     */
    public static <T> void validateProperty(T object, String propertyName, Class<?>... groups) {
        if (object == null) {
            throw new ConstraintViolationException("校验对象不能为null", Collections.emptySet());
        }
        if (propertyName == null || propertyName.trim().isEmpty()) {
            throw new ConstraintViolationException("属性名不能为空", Collections.emptySet());
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validateProperty(object, propertyName, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("属性 '" + propertyName + "' 校验失败", violations);
        }
    }

    /**
     * 校验指定属性值（使用默认分组）
     *
     * @param beanType     对象类型
     * @param propertyName 属性名
     * @param value        属性值
     * @param groups       校验分组
     * @param <T>          对象类型
     * @throws ConstraintViolationException 校验失败时抛出
     */
    public static <T> void validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        if (beanType == null) {
            throw new ConstraintViolationException("对象类型不能为null", Collections.emptySet());
        }
        if (propertyName == null || propertyName.trim().isEmpty()) {
            throw new ConstraintViolationException("属性名不能为空", Collections.emptySet());
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validateValue(beanType, propertyName, value, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("属性值 '" + propertyName + "' 校验失败", violations);
        }
    }

    /* ------------------------- 批量校验方法 ------------------------- */

    /**
     * 批量校验多个对象
     *
     * @param objects 要校验的对象集合
     * @param groups  校验分组
     * @param <T>     对象类型
     * @throws ConstraintViolationException 如果有任何一个对象校验失败
     */
    public static <T> void validateAll(Collection<T> objects, Class<?>... groups) {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        Set<ConstraintViolation<?>> allViolations = new HashSet<>();
        for (T object : objects) {
            if (object != null) {
                Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
                allViolations.addAll(violations);
            }
        }

        if (!allViolations.isEmpty()) {
            throw new ConstraintViolationException("批量校验失败", allViolations);
        }
    }

    /**
     * 批量校验多个对象，返回所有错误信息
     *
     * @param objects 要校验的对象集合
     * @param groups  校验分组
     * @param <T>     对象类型
     * @return 校验结果集合，key为对象索引，value为错误信息列表
     */
    public static <T> Map<Integer, List<String>> validateAllWithDetails(Collection<T> objects, Class<?>... groups) {
        Map<Integer, List<String>> result = new HashMap<>();

        if (objects == null || objects.isEmpty()) {
            return result;
        }

        int index = 0;
        for (T object : objects) {
            if (object != null) {
                Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
                if (!violations.isEmpty()) {
                    List<String> errorMessages = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList());
                    result.put(index, errorMessages);
                }
            }
            index++;
        }

        return result;
    }

    /* ------------------------- 快速校验方法（返回布尔值） ------------------------- */

    /**
     * 快速校验对象是否有效
     *
     * @param object 要校验的对象
     * @param groups 校验分组
     * @param <T>    对象类型
     * @return true: 校验通过, false: 校验失败
     */
    public static <T> boolean isValid(T object, Class<?>... groups) {
        if (object == null) {
            return false;
        }
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        return violations.isEmpty();
    }

    /**
     * 快速校验对象属性是否有效
     *
     * @param object       要校验的对象
     * @param propertyName 属性名
     * @param groups       校验分组
     * @param <T>          对象类型
     * @return true: 校验通过, false: 校验失败
     */
    public static <T> boolean isPropertyValid(T object, String propertyName, Class<?>... groups) {
        if (object == null || propertyName == null || propertyName.trim().isEmpty()) {
            return false;
        }
        Set<ConstraintViolation<T>> violations = VALIDATOR.validateProperty(object, propertyName, groups);
        return violations.isEmpty();
    }

    /* ------------------------- 获取校验结果（不抛出异常） ------------------------- */

    /**
     * 获取校验错误信息（不抛出异常）
     *
     * @param object 要校验的对象
     * @param groups 校验分组
     * @param <T>    对象类型
     * @return 错误信息列表，如果校验通过返回空列表
     */
    public static <T> List<String> getValidationMessages(T object, Class<?>... groups) {
        if (object == null) {
            return Collections.singletonList("校验对象不能为null");
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    /**
     * 获取校验错误详细信息（不抛出异常）
     *
     * @param object 要校验的对象
     * @param groups 校验分组
     * @param <T>    对象类型
     * @return 错误详细信息列表
     */
    public static <T> List<ViolationDetail> getValidationDetails(T object, Class<?>... groups) {
        if (object == null) {
            return Collections.singletonList(new ViolationDetail(null, "object", "校验对象不能为null", null));
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        return violations.stream()
                .map(violation -> new ViolationDetail(
                        violation.getRootBeanClass(),
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .collect(Collectors.toList());
    }

    /* ------------------------- 分组校验相关 ------------------------- */

    /**
     * 按顺序校验多个分组（遇到第一个失败分组即停止）
     *
     * @param object 要校验的对象
     * @param groups 按顺序校验的分组数组
     * @param <T>    对象类型
     * @throws ConstraintViolationException 校验失败时抛出
     */
    public static <T> void validateInSequence(T object, Class<?>[] groups) {
        if (object == null) {
            throw new ConstraintViolationException("校验对象不能为null", Collections.emptySet());
        }
        if (groups == null || groups.length == 0) {
            validate(object);
            return;
        }

        for (Class<?> group : groups) {
            Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, group);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("分组 [" + group.getSimpleName() + "] 校验失败", violations);
            }
        }
    }

    /* ------------------------- 日志记录校验 ------------------------- */

    /**
     * 校验对象并记录日志（不抛出异常）
     *
     * @param object  要校验的对象
     * @param message 日志前缀
     * @param groups  校验分组
     * @param <T>     对象类型
     * @return true: 校验通过, false: 校验失败
     */
    public static <T> boolean validateWithLog(T object, String message, Class<?>... groups) {
        if (object == null) {
            log.warn("{}: 校验对象为空", message);
            return false;
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (!violations.isEmpty()) {
            log.warn("{}: 校验失败 - {}", message,
                    violations.stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .collect(Collectors.joining(", ")));
            return false;
        }

        return true;
    }

    /**
     * 校验对象属性并记录日志（不抛出异常）
     *
     * @param object       要校验的对象
     * @param propertyName 属性名
     * @param message      日志前缀
     * @param groups       校验分组
     * @param <T>          对象类型
     * @return true: 校验通过, false: 校验失败
     */
    public static <T> boolean validatePropertyWithLog(T object, String propertyName, String message, Class<?>... groups) {
        if (object == null) {
            log.warn("{}: 校验对象为空", message);
            return false;
        }
        if (propertyName == null || propertyName.trim().isEmpty()) {
            log.warn("{}: 属性名为空", message);
            return false;
        }

        Set<ConstraintViolation<T>> violations = VALIDATOR.validateProperty(object, propertyName, groups);
        if (!violations.isEmpty()) {
            log.warn("{}: 属性 '{}' 校验失败 - {}", message, propertyName,
                    violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(", ")));
            return false;
        }

        return true;
    }

    /* ------------------------- 内部类 ------------------------- */

    /**
     * 校验错误详细信息
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Data
    public static class ViolationDetail {
        /**
         * 校验的Bean类型
         */
        private Class<?> beanClass;

        /**
         * 属性路径
         */
        private String propertyPath;

        /**
         * 错误消息
         */
        private String message;

        /**
         * 无效的值
         */
        private Object invalidValue;

        public ViolationDetail(Class<?> beanClass, String propertyPath, String message, Object invalidValue) {
            this.beanClass = beanClass;
            this.propertyPath = propertyPath;
            this.message = message;
            this.invalidValue = invalidValue;
        }

        @Override
        public String toString() {
            return String.format("%s.%s: %s (值: %s)",
                    beanClass != null ? beanClass.getSimpleName() : "null",
                    propertyPath,
                    message,
                    invalidValue != null ? invalidValue.toString() : "null");
        }
    }
}