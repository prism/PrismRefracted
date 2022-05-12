package network.darkhelmet.prism.commands;

import network.darkhelmet.prism.Il8nHelper;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actionlibs.RecordingTask;
import network.darkhelmet.prism.commandlibs.CallInfo;

import java.util.ArrayList;
import java.util.List;

public class SettingCommands extends AbstractCommand {
    @Override
    public void handle(CallInfo call) {
        if (call.getArgs().length > 1) {
            switch (call.getArg(0).toLowerCase()) {
                case "batchsize":
                    int actions = Integer.parseInt(call.getArg(1));
                    RecordingTask.setActionsPerInsert(actions);
                    Prism.messenger.sendMessage(call.getSender(),
                            Prism.messenger.playerHeaderMsg(Il8nHelper.formatMessage("command-settings-batchsize-set", actions)));
                    return;
                default:
                    //todo add feedback
            }
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        List<String> result = new ArrayList<>();
        SWITCH:
        switch (call.getArgs().length) {
            case 1:
                result.add("batchsize");
                break;
            case 2:
                switch (call.getArg(0).toLowerCase()) {
                    case "batchsize":
                        result.add(String.valueOf(RecordingTask.getActionsPerInsert()));
                        break SWITCH;
                }
        }
        return result;
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
