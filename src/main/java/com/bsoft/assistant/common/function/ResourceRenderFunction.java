package com.bsoft.assistant.common.function;


import com.bsoft.assistant.common.projectenum.IResourceEnum;

/**
 * Created by
 * tuz
 * on 2021/12/17.
 */
@FunctionalInterface
public interface ResourceRenderFunction<Code> {
    IResourceEnum parse(Code enumCode);
}

