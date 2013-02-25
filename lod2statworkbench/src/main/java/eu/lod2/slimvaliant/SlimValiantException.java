package eu.lod2.slimvaliant;


public class SlimValiantException extends Exception {

    SlimValiantException () {
    };

    SlimValiantException(String msg) {
      super(msg);
    };

    SlimValiantException(String msg, Exception e) {
      super(msg +  e.getMessage());
    };

};


