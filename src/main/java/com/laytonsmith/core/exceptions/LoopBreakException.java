/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.laytonsmith.core.exceptions;

/**
 *
 * @author Layton
 */
public class LoopBreakException extends ProgramFlowManipulationException{
    int number;
    public LoopBreakException(int times){
        number = times;
    }
    public int getTimes(){
        return number;
    }    
    public void setTimes(int number){
        this.number = number;
    }
}
