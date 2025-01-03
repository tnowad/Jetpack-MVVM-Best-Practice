package com.kunminx.puremusic.domain.message;

import com.kunminx.architecture.domain.dispatch.MviDispatcher;
import com.kunminx.puremusic.domain.event.Messages;

/**
 * TODO:Note 2022.07.04
 *  `
 *  PageMessenger is a domain layer component that can be used for "cross-page communication" scenarios,
 *  for example, after navigating to the login page and completing the login, the login page notifies other pages to refresh their states,
 * <p>
 * PageMessenger implements reliable message push based on MVI-Dispatcher,
 *  ensuring that "messages are consumed and only consumed once" through message queues, reference counting, and other designs,
 *  and through cohesive design, it completely eliminates issues such as mutable abuse,
 * <p>
 * Given that the potential of MVI-Dispatcher is hard to leverage in this project scenario, it is currently used to transform DownloadRequester and SharedViewModel as an example,
 *  and by comparing SharedViewModel and PageMessenger, it becomes clear that the latter can elegantly and concisely implement reliable and consistent message distribution,
 * <p>
 * <p>
 * For more details, refer to the domain layer example written specifically for MVI-Dispatcher:
 * <p>
 * https://github.com/KunMinX/MVI-Dispatcher
 * <p>
 * Created by KunMinX on 2022/7/4
 */
public class PageMessenger extends MviDispatcher<Messages> {
    @Override
    protected void onHandle(Messages event) {
        sendResult(event);
    }
}
