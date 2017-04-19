package com.acme.realmcomponenttodo;

public interface Component<T extends Action> {

    void performAction(T action);

}