
package chatapp;

import java.io.Serializable;
import java.util.LinkedList;

public class Chat  {
    ContactNode c;

   
    public ContactNode getContact() {
        return c;
    }

    public Chat(ContactNode c) {
        this.c=c;
        
    }
}


class ContactNode  {
 
    String name;
    int number;

    public ContactNode(String name, int number) {
        this.name = name;
        this.number = number;

    }
}
