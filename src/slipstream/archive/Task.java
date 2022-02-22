package slipstream.archive;

/**
 * Stashed old code for reference, amusement, and inspiration
 */
public class Task extends slipstream.untidy.taskdb.Task {
    /*public boolean addPermPre(Task task, boolean toggle_include_validation) {
        if(toggle_include_validation) {
            if(this.eq(task.getNAME())) {
                System.out.println(" * * * '" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Rule @" + task.getNAME() + " -> " + getNAME() + ": I think these two tasks are the same.");
                return false;
            }
            if(findPermPre(task.getNAME()) != null) {
                System.out.println(" * * * '" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Rule @" + task.getNAME() + " -> " + getNAME() + ": I think this task is already somewhere above");
                return false;
            }
            if(task.findPermPost(NAME.get()) != null) {
                System.out.println(" * * * '" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Rule @" + task.getNAME() + " -> " + getNAME() + ": I think this task is already somewhere below");
                return false;
            }
            if(!getPermPres().isEmpty()) {
                for(slipstream.untidy.taskdb.Task x : getPermPres()) {
                    if(task.findAbove(x.getNAME()) != null) {
                        System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): '" + getNAME() + "' is already a pre for one of '" + x.getNAME() + "'s Perm Pres (Might be DEP)");
                        return false;
                    }
                }
            }
        }
        getPres().forEach(x -> {
            if(task.findAbove(x.getNAME()) != null) {
                System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Removing pre from '" + getNAME() + "' that is a pre of A: " + x.getNAME() + "'");
                removePre(x);
            }
        });
        task.getPosts().forEach(x -> {
            if(findBelow(x.getNAME()) != null) {
                System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Removing post from '" + getNAME() + "' that is a post of '" + x.getNAME() + "'");
                task.removePost(x);
            }
        });
        getPres().forEach(x -> {
            if(x.eq(task.getNAME())) {
                System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Removing existing non-Perm rule because Perm rule will replace it");
                removePre(x);
            }
        });
        task.getPermPosts().forEach(x -> {
            System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Moving A's Perm Posts to B's Perm Posts");
            System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Removing Perm Post from A");
            task.removePermPost(x);
            System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Adding Perm Post to B.");
            addBelowPermLink(x, toggle_include_validation);
        });
        if(!getPermPres().isEmpty()) {
            System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Moving B's Perm Pres to A's Perm Pres");
            getPermPres().forEach(x -> {
                System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Removing Perm Pre from B.");
                removePermPre(x);
                System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Adding Perm Pre to A.");
                task.addPermPre(x);
            });
        }
        if(task.getPermPres().isEmpty()) {
            System.out.println("   Flagging Perm: " + task.getNAME());
            task.flagPerm();
        }
        pushPermPre(task);
        task.pushPermPost(this);
        if(task.getPermPres().isEmpty()) {
            task.pushPermPre(this);
        }
        if(getPermPosts().isEmpty()) {
            pushPermPost(task);
        }
        if(getPermPost(0).eq(task.getNAME())) {
            System.out.println("   Flagging Perm: " + task.getNAME());
            task.flagPerm();
        }
        System.out.println("'" + getNAME() + "'.addPermPre('" + task.getNAME() + "'): Finished.");
        return true;
    }

    public boolean addBelowPermLink(slipstream.untidy.taskdb.Task task, boolean toggle_include_validation) {
        if(toggle_include_validation) {
            System.out.println("'" + getNAME() + "'.addBelowPermLink('" + task.getNAME() + "'): Validating Perm Post before Adding.");
            if(this.eq(task.getNAME())) {
                System.out.println(" * * * I think these two tasks are the same.");
                return false;
            }
            if(findPermPost(task.getNAME()) != null) {
                System.out.println(" * * * I think this task is already somewhere below");
                return false;
            }
            if(task.findPermPre(NAME.get()) != null) {
                System.out.println(" * * * I think this task is already somewhere above");
                return false;
            }
            if(!getPermPosts().isEmpty()) {
                System.out.println("Checking if B is already a pre for any of A's Perm Posts (Might be DEP)");
                for(slipstream.untidy.taskdb.Task t : getPermPosts()) {
                    if(task.findBelow(t.getNAME()) != null) {
                        return false;
                    }
                }
            }
        }
        for(int i = 0; i < getPosts().size(); i++) {
            if(task.findBelow(getPost(i).getNAME()) != null) {
                syso("Removing post '" + getPost(i).getNAME() + "' from '" + getNAME() + "' that is a post of '" + task.getNAME() + "'");
                removePost(getPost(i));
            }
        }
        for(int i = 0; i < task.getPres().size(); i++) {
            if(findAbove(task.getPre(i).getNAME()) != null) {
                syso("Removing pre '" + task.getPre(i).getNAME() + "' from '" + getNAME() + "' that is a pre of '" + task.getNAME() + "'");
                task.removePre(task.getPre(i));
            }
        }
        for(int i = 0; i < getPosts().size(); i++) {
            if(getPost(i).eq(task.getNAME())) {
                syso("Removing existing non-Perm rule because the new Perm rule will replace it");
                removePost(getPost(i--));
            }
        }
        if(!task.getPermPres().isEmpty()) {
            System.out.println("Moving '" + task.getNAME() + "'s Perm Pres to '" + getNAME() + "'s Perm Pres");
            task.getPermPres().forEach(x -> {
                System.out.println("Moving '" + task.getNAME() + "'s Perm Pre: '" + x.getNAME() + "'");
                if(!x.getPermPosts().contains(task)) {
                    System.out.println(" * * * Alert: '" + x.getNAME() + "'s perm posts does not contain '" + task.getNAME() + "'");
                }
                System.out.println("Removing Perm Pre '" + x.getNAME() + "' from '" + task.getNAME() +"'.");
                task.removePermPre(x);
                System.out.println("Adding Perm Pre: '" + getNAME() + "' <-P- '" + x.getNAME() + "'.'");
                addPermPre(x);
            });
        }
        if(!getPermPosts().isEmpty()) {
            System.out.println("Moving '" + getNAME() + "'s Perm Posts to '" + task.getNAME() + "'s Perm Posts");
            getPermPosts().forEach(x -> {
                System.out.println("Moving '" + getNAME() + "'s Perm Post: '" + x.getNAME() + "'");
                if(!x.getPermPres().contains(this)) {
                    System.out.println(" * * * Alert: '" + x.getNAME() + "'s perm pres does not contain '" + getNAME() + "'");
                }
                System.out.println("Removing Perm Post '" + x.getNAME() + "' from '" + getNAME() + "'.");
                removePermPost(x);
                System.out.println("Adding Perm Post: '" + task.getNAME() + "' -P-> " + x.getNAME() + ".'");
                task.addPermPost(x);
            });
        }
        pushPermPost(task);
        task.pushPermPre(this);

        if(task.getPermPosts().isEmpty()) {
            task.pushPermPost(this);
        }
        if(getPermPres().isEmpty()) {
            pushPermPre(task);
        }

        if(task.getPermPre(0).eq(getNAME())) {
            flagPerm();
        }
        System.out.println("'" + getNAME() + "'.addBelowPermLink('" + task.getNAME() + "'): Finished.");
        return true;
    }*/
}
