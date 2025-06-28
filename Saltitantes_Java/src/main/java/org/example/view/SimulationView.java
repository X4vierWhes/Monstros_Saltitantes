package org.example.view;

import org.example.controller.SimulationController;

public class SimulationView {

    SimulationController simulationScreen;

    public boolean initSimulationView(){
        try{
            simulationScreen = new SimulationController();
            simulationScreen.initScreen();
        }catch (Exception e) {
            return false;
        }
        return true;
    }
}
