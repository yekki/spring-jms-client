package me.yekki.jms.spring.cmd;

import org.springframework.stereotype.Service;

@Service("StoreAdmin")
public class StoreAdminCommand implements Command {

    @Override
    public void execute() {
        weblogic.store.Admin.main(null);
    }
}
