/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.stk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.internal.telephony.TelephonyConstants;
import com.android.internal.telephony.cat.AppInterface;
import com.android.internal.telephony.cat.CatLog;

/**
 * Receiver class to get STK intents, broadcasted by telephony layer.
 *
 */
public class StkCmdReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int slotId = intent.getIntExtra(TelephonyConstants.EXTRA_SLOT, 0);
        if (!StkApp.isMsgForMe(slotId)) {
            CatLog.d(this, "STK1, ignore msg from SLOT: " + slotId);
            return;
        }

        String action = intent.getAction();
        CatLog.d(this, "STK1 accepts msg for SLOT: " + slotId + "; action: " +action);

        if (action.equals(AppInterface.CAT_CMD_ACTION)) {
            handleCommandMessage(context, intent);
        } else if (action.equals(AppInterface.CAT_SESSION_END_ACTION)) {
            handleSessionEnd(context, intent);
        } else if (action.equals(AppInterface.USER_ACTIVITY_AVAILABLE_ACTION)) {
            handleUserActivityAvailable(context, intent);
        }
    }

    private void handleCommandMessage(Context context, Intent intent) {
        Bundle args = new Bundle();
        args.putInt(StkAppService.OPCODE, StkAppService.OP_CMD);
        args.putParcelable(StkAppService.CMD_MSG, intent
                .getParcelableExtra("STK CMD"));
        context.startService(new Intent(context, StkAppService.class)
                .putExtras(args));
    }

    private void handleSessionEnd(Context context, Intent intent) {
        Bundle args = new Bundle();
        args.putInt(StkAppService.OPCODE, StkAppService.OP_END_SESSION);
        context.startService(new Intent(context, StkAppService.class)
                .putExtras(args));
    }

    private void handleUserActivityAvailable(Context context, Intent intent) {
        Bundle args = new Bundle();
        args.putInt(StkAppService.OPCODE, StkAppService.OP_USER_ACTIVITY);
        context.startService(new Intent(context, StkAppService.class)
                .putExtras(args));
    }
}
