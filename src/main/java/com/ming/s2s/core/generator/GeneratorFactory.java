package com.ming.s2s.core.generator;

import com.ming.s2s.common.exception.BusinessException;
import com.ming.s2s.model.metadata.TechStackConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for creating code generator strategies based on technology stack
 */
@Slf4j
@Component
public class GeneratorFactory {

    private final List<CodeGenerator> generators;

    @Autowired
    public GeneratorFactory(List<CodeGenerator> generators) {
        this.generators = generators;
        log.info("Initialized GeneratorFactory with {} generator(s)", generators.size());
    }

    /**
     * Get generator strategy for the given technology stack
     *
     * @param techStack Technology stack configuration
     * @return CodeGenerator strategy implementation
     * @throws BusinessException if no generator found for the technology stack
     */
    public CodeGenerator getGenerator(TechStackConfig techStack) {
        if (techStack == null) {
            techStack = new TechStackConfig(); // Default config
        }

        String ormFramework = techStack.getOrmFramework();
        String buildTool = techStack.getBuildTool();

        return generators.stream()
            .filter(generator -> generator.supports(ormFramework, buildTool))
            .findFirst()
            .orElseThrow(() -> new BusinessException(
                String.format("No code generator found for technology stack: %s + %s. " +
                    "Supported combinations: mybatis + maven", ormFramework, buildTool)
            ));
    }

    /**
     * Get generator by explicit ORM framework and build tool
     *
     * @param ormFramework ORM framework (mybatis, jpa, mybatis-plus)
     * @param buildTool Build tool (maven, gradle)
     * @return CodeGenerator strategy implementation
     */
    public CodeGenerator getGenerator(String ormFramework, String buildTool) {
        TechStackConfig techStack = new TechStackConfig();
        techStack.setOrmFramework(ormFramework);
        techStack.setBuildTool(buildTool);
        return getGenerator(techStack);
    }
}
