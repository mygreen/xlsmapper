package com.gh.mygreen.xlsmapper.spring;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 *
 * @author T.TSUCHIE
 *
 */
@Service
public class SampleService {
    
    public void doService() {
        System.out.println("do service.");
    }
}
