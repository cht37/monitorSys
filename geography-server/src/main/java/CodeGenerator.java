import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) throws IOException {
        generate();
    }
    private  static void generate() throws IOException {
        File directory = new File("");
        String courseFile = "F:\\documents\\Work\\monitorSys\\geography-server";

//            **********************必须修改的数据库连接参数**************
        String dataUrl="jdbc:mysql://localhost:3306/monitor_sys?characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false&allowMultiQueries=true";
        String userName="root";
        String password="cht021125";

        FastAutoGenerator.create(dataUrl,userName ,password )
                .globalConfig(builder -> {
                    builder.author("chen hua teng") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(courseFile+"\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.neu.monitorSys.geography") // 设置父包名
//                            .moduleName("MemberService") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, courseFile+"\\src\\main\\resources\\com\\neu\\monitorSys\\geography\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
//                    builder.mapperBuilder().enableMapperAnnotation().build();
                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
                            .enableRestStyle();  // 开启生成@RestController 控制器

//                    **********************必须修改的表名**************
                    builder.addInclude("provinces").addInclude("cities").addInclude("grid_manager_area");// 设置需要生成的表名


//                            .addTablePrefix("t_", "sys_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}