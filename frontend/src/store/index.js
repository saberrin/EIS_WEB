// src/store/index.js
import {
    createStore
} from 'vuex';

function getRFC3339Date(daysAgo) {
    const date = new Date();
    date.setUTCDate(date.getUTCDate() - daysAgo);
    return date.toISOString();
}
const store = createStore({
    state() {
        return {
            CurrentComponent: 'ThreeView',
            CurrentModelName: 52, //所选pack  52  104两种 修改此处自动切换
            CurrentContainer: 1, //所选集装箱
            CurrentCellData: {}, //当前cell全部数据
            dateFrom: getRFC3339Date(6),
            dateTo: new Date().toISOString(),
            showTimeChose: false,
            showAbout: false,
            ExportStartTime: null,
            ExportEndTime: null,

        };
    },
    mutations: {
        setCurrentComponent(state, CurrentComponent) {
            state.CurrentComponent = CurrentComponent;
        },
        setCurrentModelName(state, CurrentModelName) {
            state.CurrentModelName = CurrentModelName;
        },
        setCurrentContainer(state, CurrentContainer) {
            state.CurrentContainer = CurrentContainer;
        },
        setCurrentCellData(state, CurrentCellData) {
            state.CurrentCellData = CurrentCellData;
        },
        setdateFrom(state, dateFrom) {
            state.dateFrom = dateFrom;
        },
        setdateTo(state, dateTo) {
            state.dateTo = dateTo;
        },
        setshowTimeChose(state, showTimeChose) {
            state.showTimeChose = showTimeChose;
        },
        setshowAbout(state, showAbout) {
            state.showAbout = showAbout;
        },
        setExportStartTime(state, ExportStartTime) {
            state.ExportStartTime = ExportStartTime;
        },
        setExportEndTime(state, ExportEndTime) {
            state.ExportEndTime = ExportEndTime;
        },
    },
});

export default store;