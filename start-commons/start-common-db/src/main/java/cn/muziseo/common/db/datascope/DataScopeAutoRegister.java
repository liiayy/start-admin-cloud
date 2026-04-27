package cn.muziseo.common.db.datascope;
 
import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.db.annotation.DataColumn;
import com.mybatisflex.annotation.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
 
import java.lang.reflect.Field;
 
/**
 * 数据权限自动注册器
 * <p>
 * 启动时自动扫描指定包下的 Entity，解析 @DataColumn 注解并注册到 DataScopeContext。
 *
 * @author 木子软件
 */
@Slf4j
@AutoConfiguration
public class DataScopeAutoRegister implements InitializingBean, ResourceLoaderAware {
 
    @Value("${start.datascope.scan-package:cn.muziseo.service}")
    private String scanPackage;
 
    private ResourceLoader resourceLoader;
 
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
 
    @Override
    public void afterPropertiesSet() {
        log.info("[数据权限] 开始扫描实体类注册数据权限字段: package={}", scanPackage);
        try {
            scanAndRegister();
        } catch (Exception e) {
            log.error("[数据权限] 扫描实体类异常", e);
        }
    }
 
    private void scanAndRegister() throws Exception {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
 
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(scanPackage) + "/**/*.class";
 
        Resource[] resources = resolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> clazz = ClassUtils.forName(className, resourceLoader.getClassLoader());
 
                // 检查是否有 @Table 注解
                Table tableAnno = clazz.getAnnotation(Table.class);
                if (tableAnno != null) {
                    processClass(clazz, tableAnno.value());
                }
            }
        }
    }
 
    private void processClass(Class<?> clazz, String tableName) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            DataColumn dataColumn = field.getAnnotation(DataColumn.class);
            if (dataColumn != null) {
                // 确定列名：优先使用 alias，其次使用字段名转下划线（MyBatis-Flex 默认策略）
                String columnName = dataColumn.alias();
                if (StrUtil.isEmpty(columnName)) {
                    columnName = StrUtil.toUnderlineCase(field.getName());
                }
 
                DataScopeContext.register(tableName, dataColumn.value(), columnName);
                log.info("[数据权限] 自动注册: table={}, type={}, column={}",
                        tableName, dataColumn.value(), columnName);
            }
        }
    }
}
