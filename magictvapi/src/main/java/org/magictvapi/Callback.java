package org.magictvapi;

/**
 * Created by thomas on 11/03/2016.
 *
 * Callback used for async mathod call
 */
public interface Callback<T> {
    /**
     * methods for eun callback
     * @param params
     */
    void call(T param);
}
