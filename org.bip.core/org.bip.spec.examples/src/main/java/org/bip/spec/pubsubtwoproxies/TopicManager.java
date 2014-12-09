package org.bip.spec.pubsubtwoproxies;

import java.util.HashMap;

import org.bip.annotations.ComponentType;
import org.bip.annotations.Data;

@ComponentType(initial = "0", name = "org.bip.spec.TopicManager")
public class TopicManager {
	
    private HashMap<String, Topic> topics;

    public TopicManager() {
    	this.topics = new HashMap<String, Topic>();
    }
    
	// Transition for port execute.
	public void executeCommand(@Data(name = "value") Command command) {

        switch(command.getId()){
        case SUBSCRIBE:
            subscribe(command.getClient(),command.getTopic());
            break;
        case UNSUBSCRIBE:
            unsubscribe(command.getClient(),command.getTopic());
            break;
        case PUBLISH:
            publish(command.getClient(), command.getTopic(),command.getMessage());
            break;
        default:
            break;
        }
        
    }
	
	private void subscribe(ClientProxy client, String topicName) {
    	
        Topic topic = topics.get(topicName);
		// topic.addClient(client);
        
    }
    
	private void unsubscribe(ClientProxy client, String topicName) {

        Topic topic = topics.get(topicName);
		// topic.removeClient(client);

    }
         
	private void publish(ClientProxy client, String topicName, String message) {

        Topic topic = topics.get(topicName);
        topic.publish(client, message);

    }


}