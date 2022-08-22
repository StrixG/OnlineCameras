package com.obrekht.onlinecameras.view;

import java.util.List;

import moxy.MvpView;
import moxy.viewstate.ViewCommand;
import moxy.viewstate.strategy.StateStrategy;

public class ClearStateStrategy implements StateStrategy {

    @Override
    public <View extends MvpView> void beforeApply(List<ViewCommand<View>> list, ViewCommand<View> viewCommand) {
        list.clear();
    }

    @Override
    public <View extends MvpView> void afterApply(List<ViewCommand<View>> list, ViewCommand<View> viewCommand) {

    }
}
