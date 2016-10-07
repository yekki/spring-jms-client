package me.yekki.jms.spring.utils;

public class MessageCounter {

    private int threads;
    private int total;
    private int messagesPerThread;
    private int leftMessages;

    public MessageCounter(int totalMessage, int threadCount) {
        this.total = totalMessage;
        this.threads = threadCount;

        messagesPerThread = total / threads;
        leftMessages = total - threads * messagesPerThread;

        if (leftMessages > 0) {
            this.threads--;
            messagesPerThread = total / threads;
            leftMessages = total - threads * messagesPerThread;
        }

    }

    public int getLeftMessageCount() {

        return leftMessages;
    }

    public int getMessageCountPerThread() {

        return messagesPerThread;
    }

    public boolean isLastBatch(int i) {

        return (i == getThreadCount()) && (getLeftMessageCount() != 0);
    }

    public int getThreadCount() {

        if (leftMessages == 0)
            return threads;
        else
            return threads + 1;
    }

    public String toString() {

        return String.format("threads=%d, messages per thread=%d, left messages=%d", getThreadCount(), getMessageCountPerThread(), getLeftMessageCount());
    }
}
