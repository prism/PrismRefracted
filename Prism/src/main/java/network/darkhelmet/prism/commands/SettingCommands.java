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
        if (call.getArgs().length > 2) {
            switch (call.getArg(1).toLowerCase()) {
                case "batchsize":
                case "批次大小":
                    int actions = Integer.parseInt(call.getArg(2));
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
            case 2:
                result.add("batchsize");
                result.add("批次大小");
                break;
            case 3:
                switch (call.getArg(1).toLowerCase()) {
                    case "batchsize":
                    case "批次大小":
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
