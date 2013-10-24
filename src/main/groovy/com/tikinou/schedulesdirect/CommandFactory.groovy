package com.tikinou.schedulesdirect

/**
 * @author: Sebastien Astie
 */
class CommandFactory {
    def static concreteFactory
    def static getCommand(actionType, objectType) { concreteFactory.getCommand(actionType, objectType)}
}
