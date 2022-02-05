package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.actionlibs.RecordingTask;
import network.darkhelmet.prism.commandlibs.CallInfo;

import java.util.List;

public class SettingCommands extends AbstractCommand {
    @Override
    public void handle(CallInfo call) {
        if (call.getArgs().length > 1) {
            switch (call.getArg(0).toLowerCase()) {
                case "batchsize":
                case "批次大小":
                    int actions = Integer.parseInt(call.getArg(1));
                    RecordingTask.setActionsPerInsert(actions);
                    //todo add some feedback
                    return;
                default:
                    //todo add some feedback
            }
        }
        //todo add feedback
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    @Override
    public String[] getHelp() {
        return new String[]{Il8nHelper.getRawMessage("help-settings")};
    }

    @Override
    public String getRef() {
        return "/settings.html";
    }
}
