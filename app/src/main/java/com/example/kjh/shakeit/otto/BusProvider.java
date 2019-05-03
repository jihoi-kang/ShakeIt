package com.example.kjh.shakeit.otto;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public final class BusProvider extends Bus {

    private static Bus instance;

    public static Bus getInstance() {
        if (instance == null)
            instance = new Bus(ThreadEnforcer.ANY);

        return instance;
    }

}
