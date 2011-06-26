/*
 * Copyright 2009-2011 Universität Duisburg-Essen, Working Group
 * "Information Engineering"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unidue.inf.is.ezdl.dlservices.library.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeight;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.GroupList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.OnlineReferenceSystem;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemFactory;
import de.unidue.inf.is.ezdl.dlservices.library.store.LibraryStore;
import de.unidue.inf.is.ezdl.dlservices.library.store.LibraryStoreFactory;



/** Manages the whole Library. */
public class LibraryManager {

    private static Logger logger = Logger.getLogger(LibraryManager.class);

    private LibraryStore libraryStore;
    private Properties props;


    /**
     * Initializes the LibraryManager
     * 
     * @param store
     *            The desired store. For example LibraryFactory.DBStore
     * @param props
     *            Propertie File with settings for the store
     */
    public LibraryManager(String store, Properties props) {
        libraryStore = LibraryStoreFactory.getLibraryStore(store, props);
        this.props = props;
    }


    /** Returns the properties */
    public Properties getProperties() {
        return props;
    }


    /** Returns the library store */
    public LibraryStore getLibraryStore() {
        return libraryStore;
    }


    /**
     * Returns a List of available reference systems
     * 
     * @return List of available reference systems with the required Auth
     *         parameters
     */
    public List<ReferenceSystem> getAvailableReferenceSystems() {
        return ReferenceSystemFactory.getAvailableReferenceSystems();
    }


    /**
     * Returns the library as a List of Document Objects, local store and online
     * store if exists
     */
    public List<Document> getLibrary(int userId, ReferenceSystem referenceSystem) throws Exception {
        return getLibrary(userId, referenceSystem, null);
    }


    /**
     * Returns the library as a List of Document Objects, local store and online
     * stores, for each document is checked, if the groups, in which it is a
     * member, still exist. otherwise membership will be deleted
     */
    public List<Document> getLibrary(int userId, ReferenceSystem referenceSystem, List<Group> groups) throws Exception {
        List<Document> localDocs;
        List<Document> onlineDocs;

        // Get library from local store
        localDocs = libraryStore.getLibrary(userId);

        if (!referenceSystem.workOffline()) {
            // Retrieve library from online referenceSystem
            OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
            onlineDocs = rs.getReferences();
            return synchronizeDocuments(localDocs, onlineDocs, userId, referenceSystem, groups);
        }
        else {
            return localDocs;
        }
    }


    /** synchronizes the documents in the two stores, local and online store */
    private List<Document> synchronizeDocuments(List<Document> localDocs, List<Document> onlineDocs, int userId,
                    ReferenceSystem referenceSystem, List<Group> groups) throws Exception {

        // Check if groups still exists in which the documents are members
        // If not, remove membership of the documents
        // For example. Online group was deleted online. So membership has to be
        // removed
        if (groups != null) {
            for (Document doc : localDocs) {
                GroupList gl = (GroupList) doc.getFieldValue(Field.GROUPS);
                if (gl != null) {
                    List<Group> removeGroups = new ArrayList<Group>();
                    for (Group dg : gl) {
                        boolean found = false;

                        for (Group ag : groups) {
                            if (dg.getId().equals(ag.getId())) {
                                found = true;
                                break;
                            }
                            else if (dg.onlineGroup() && ag.getReferenceSystemId() != null
                                            && ag.getReferenceSystemId().equals(dg.getReferenceSystemId())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            removeGroups.add(dg);
                            this.updateDocumentLocalStore(doc, userId);
                        }
                    }

                    gl.removeAll(removeGroups);
                }
            }
        }

        // Check if there are references in the online store which are not in
        // the local store
        for (Document od : onlineDocs) {
            // If document is member of an public group, do not save it in local
            // store
            if (!checkDocumentPublicGroup(od)) {
                // Check if oid already in localDocs
                boolean found = false;
                String odid = (String) od.getFieldValue(Field.REFERENCESYSTEMID);

                for (Document ld : localDocs) {
                    boolean found2 = false;
                    String ldid = (String) ld.getFieldValue(Field.REFERENCESYSTEMID);

                    if (odid != null && ldid != null && ldid.equals(odid)) {
                        found = true;
                        found2 = true;
                    }
                    else if (ldid == null || ldid.trim().length() == 0) {
                        // The online referencesystemid of the local document is
                        // not set
                        // Now check if title, year and author is the same. If
                        // so, the document already exist
                        // in online store. Only the IDs has to be updated

                        if (od.getTitle().equals(ld.getTitle())) {
                            // Same title
                            if (od.getYear() == ld.getYear()) {
                                // same year. It is the same document!
                                ld.setFieldValue(Field.REFERENCESYSTEMID, od.getFieldValue(Field.REFERENCESYSTEMID));
                                ld.setFieldValue(Field.REFERENCESYSTEM, od.getFieldValue(Field.REFERENCESYSTEM));

                                // Update the document in the local store
                                updateDocumentLocalStore(ld, userId);
                                found = true;
                                found2 = true;
                            }
                        }
                    }

                    if (found2) {
                        // Compare content of references like TAGs, GROUP
                        // Membership and so on

                        // Groups
                        GroupList groupsLD = (GroupList) ld.getFieldValue(Field.GROUPS);
                        GroupList groupsOD = (GroupList) od.getFieldValue(Field.GROUPS);

                        if (groupsLD != null) {
                            for (Group gld : groupsLD) {
                                if (gld.onlineGroup()) {
                                    // Group is an online Group

                                    // Loook if online document also has this
                                    // membership
                                    if (groupsOD == null || !groupsOD.contains(gld.getId(), gld.getReferenceSystemId())) {
                                        // Update document in onlineStore
                                        updateDocument(ld, userId, referenceSystem);
                                        od.setFieldValue(Field.REFERENCESYSTEMID,
                                                        ld.getFieldValue(Field.REFERENCESYSTEMID));
                                    }
                                }
                            }
                        }
                        else if (groupsOD != null) {
                            // onlinedocument is saved in groups. local not.
                            // update local
                            groupsLD = new GroupList();
                            groupsLD.addAll(groupsOD);
                            updateDocumentLocalStore(ld, userId);
                        }

                        // Tags
                        TermWithWeightList tagsLD = (TermWithWeightList) ld.getFieldValue(Field.TAGS);
                        TermWithWeightList tagsOD = (TermWithWeightList) od.getFieldValue(Field.TAGS);
                        boolean updateOD = false;
                        boolean updateLD = false;

                        // synchronice local tags --> online tags
                        if (tagsLD != null) {
                            if (tagsOD != null) {
                                for (TermWithWeight tld : tagsLD) {
                                    if (!tagsOD.contains(tld.getTerm())) {
                                        tagsOD.add(tld);
                                        updateOD = true;
                                    }
                                }
                            }
                            else {
                                // lokal doc has tags, online no one, add all
                                tagsOD = new TermWithWeightList();
                                tagsOD.addAll(tagsLD);
                                od.setFieldValue(Field.TAGS, tagsOD);
                                updateOD = true;
                            }
                        }

                        // synchronize online tags --> local tags
                        if (tagsOD != null) {
                            if (tagsLD != null) {
                                for (TermWithWeight old : tagsOD) {
                                    if (!tagsLD.contains(old.getTerm())) {
                                        tagsLD.add(old);
                                        updateLD = true;
                                    }
                                }
                            }
                            else {
                                // online doc has tags, local no one, add all
                                tagsLD = new TermWithWeightList();
                                tagsLD.addAll(tagsOD);
                                updateLD = true;
                                ld.setFieldValue(Field.TAGS, tagsLD);
                            }
                        }

                        if (updateOD) {
                            updateDocumentOnlineStore(od, referenceSystem);
                            // Update referencesystemid, maybe it was changed
                            // during update process
                            ld.setFieldValue(Field.REFERENCESYSTEMID, od.getFieldValue(Field.REFERENCESYSTEMID));
                            // Update local document now too.
                            updateDocumentLocalStore(ld, userId);

                        }

                        if (updateLD) {
                            updateDocumentLocalStore(ld, userId);
                        }

                    }

                }

                if (!found) {
                    // online document not found in localstore
                    // add it
                    addToLocalStore(od, userId);
                    localDocs.add(od);
                }

            }
            else {
                localDocs.add(od);
            }
        }

        // Check if there are references in the local store which are not in the
        // online store
        for (Document ld : localDocs) {
            boolean found = false;
            String ldid = (String) ld.getFieldValue(Field.REFERENCESYSTEMID);

            for (Document od : onlineDocs) {
                String odid = (String) od.getFieldValue(Field.REFERENCESYSTEMID);

                if (odid != null && ldid != null && ldid.equals(odid)) {
                    found = true;
                }
            }

            if (!found) {
                // local document not found in onlinestore
                // add it
                addToOnlineStore(ld, userId, referenceSystem);
            }
        }

        return localDocs;
    }


    /** Returns the Document with the given OID, if not found: return is null */
    public Document getDocument(String oid, int userId) {
        return libraryStore.getDocument(oid, userId);
    }


    /** Adds a Document to the Library and online ReferenceSystem */
    public void addDocument(Document d, int userId, ReferenceSystem referenceSystem) throws Exception {
        logger.debug("Adds to Library: " + d.toString());

        // Check if document is not member of an public group
        // If yes, document is not saved in local store
        if (!checkDocumentPublicGroup(d)) {
            // Add into local Store
            addToLocalStore(d, userId);
        }

        if (!referenceSystem.workOffline()) {
            addToOnlineStore(d, userId, referenceSystem);
        }
    }


    /** adds a document to the local store */
    private void addToLocalStore(Document d, int userId) {
        libraryStore.addDocument(d, userId);
    }


    /** adds a document to the online store */
    private void addToOnlineStore(Document d, int userId, ReferenceSystem referenceSystem) throws Exception {

        OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
        rs.addReference(d);

        // Update document also in local stroe because online referenceid and
        // reference system is set
        updateDocumentLocalStore(d, userId);
    }


    /** Updates the given document in the store. Compares the two ooids */
    public void updateDocument(Document d, int userId, ReferenceSystem referenceSystem) throws Exception {
        logger.debug("Update Library: " + d.toString());

        // Update in referencesystem
        if (!referenceSystem.workOffline()) {
            updateDocumentOnlineStore(d, referenceSystem);
        }

        if (getDocument(d.getOid(), userId) != null) {
            // Document also saved in local store, update it.
            // Documents which are members in online groups are not saved in
            // local store
            updateDocumentLocalStore(d, userId);
        }
    }


    /** Update the document in the local store */
    private void updateDocumentLocalStore(Document d, int userId) {
        libraryStore.updateDocument(d, userId);
    }


    /** Updates the document in the online store */
    private void updateDocumentOnlineStore(Document d, ReferenceSystem referenceSystem) throws Exception {
        OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
        rs.updateReference(d);
    }


    /** Remove a Document from the Library */
    public void removeDocument(Document d, int userId, ReferenceSystem referenceSystem) throws Exception {
        logger.debug("Remove document from Library: " + d.toString());

        if (!referenceSystem.workOffline()) {
            OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
            rs.removeReference(d);
        }

        // Document also saved in local store, delete it.
        // Documents which are members in online groups are not saved in local
        // store
        if (getDocument(d.getOid(), userId) != null) {
            libraryStore.removeDocument(d, userId);
        }
    }


    /** Add a group */
    public void addGroup(Group group, int userId, ReferenceSystem referenceSystem) throws Exception {
        logger.debug("Add group: " + group.toString());

        // if group should saved in online referencesystem
        if (!referenceSystem.workOffline() && group.getSaveOnline()) {
            addGroupOnlineStore(group, referenceSystem);
        }
        // Add group to local store
        addGroupLocalStore(group, userId);
    }


    /** Add a group to the local store */
    private void addGroupLocalStore(Group group, int userId) {
        libraryStore.addGroup(group, userId);
    }


    /** Add a group to the online store */
    private void addGroupOnlineStore(Group group, ReferenceSystem referenceSystem) throws Exception {
        OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
        rs.addGroup(group);
    }


    /** Update group */
    public List<Document> updateGroup(Group group, int userId, ReferenceSystem referenceSystem, List<Document> documents)
                    throws Exception {
        logger.debug("Update group: " + group.toString());

        List<Document> updatedDocuments = null;

        if (!referenceSystem.workOffline() && documents != null && group.getSaveOnline()) {
            // Local group should be now online. Save the group also online and
            // add the documents
            // Group does not exist online. ADD it
            addGroupOnlineStore(group, referenceSystem);

            updatedDocuments = new ArrayList<Document>();

            // Update documents groups intern online reference ids
            for (Document d : documents) {
                GroupList gl = (GroupList) d.getFieldValue(Field.GROUPS);
                if (gl != null) {
                    Group gh = gl.getGroup(group.getId());
                    if (gh != null) {
                        // Update ids
                        gh.setReferenceSystem(group.getReferenceSystem());
                        gh.setReferenceSystemId(group.getReferenceSystemId());
                        // Update document
                        updateDocument(d, userId, referenceSystem);
                        updatedDocuments.add(d);
                    }

                }
            }
        }

        updateGroupLocalStore(group, userId);

        return updatedDocuments;

    }


    private void updateGroupLocalStore(Group group, int userId) {
        libraryStore.updateGroup(group, userId);
    }


    /** Remove a group */
    public void removeGroup(Group group, int userId, ReferenceSystem referenceSystem) throws Exception {
        logger.debug("Remove group: " + group.toString());
        removeGroupLocalStore(group, userId);

        if (group.onlineGroup() && !referenceSystem.workOffline()) {
            removeGroupOnlineStore(group, referenceSystem);
        }
    }


    /** Removes the group in the local store */
    private void removeGroupLocalStore(Group group, int userId) throws Exception {
        libraryStore.removeGroup(group, userId);
    }


    /** Removes the group in the online store */
    private void removeGroupOnlineStore(Group group, ReferenceSystem referenceSystem) throws Exception {
        OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
        rs.removeGroup(group);
    }


    /** Get the Group with the given group id (Local store) */
    public Group getGroup(Group group, int userId) {
        // Check if group is in the local store
        return libraryStore.getGroup(group.getId(), userId);
    }


    /** Returns the groups of the user */
    public List<Group> getGroups(int userId, ReferenceSystem referenceSystem) throws Exception {
        List<Group> localGroups = libraryStore.getGroups(userId);
        List<Group> onlineGroups;

        if (!referenceSystem.workOffline()) {
            OnlineReferenceSystem rs = ReferenceSystemFactory.createInstance(referenceSystem, props);
            onlineGroups = rs.getGroups();
            return synchronizeGroups(localGroups, onlineGroups, referenceSystem, userId);
        }
        else {
            return localGroups;
        }
    }


    /** Synchronizes the groups in the online Store and in the local store */
    private List<Group> synchronizeGroups(List<Group> localGroups, List<Group> onlineGroups,
                    ReferenceSystem referenceSystem, int userId) throws Exception {

        // Check if ther are private groups in the online Store which are not in
        // the localstore
        // if yes then add it to the localstore too.
        // Public groups are not saved in the local store.
        if (onlineGroups != null) {

            for (Group og : onlineGroups) {
                if (og.getType() != null && og.getType().equals(Group.TYPE_PRIVATE)) {
                    boolean found = false;

                    for (Group lg : localGroups) {
                        if (lg.getReferenceSystemId() != null && og.getReferenceSystemId() != null
                                        && lg.getReferenceSystemId().equals(og.getReferenceSystemId())) {
                            found = true;
                        }
                    }

                    // Group not found in local store. Add it
                    if (!found) {
                        localGroups.add(og);
                        addGroupLocalStore(og, userId);
                    }
                }
                else {
                    localGroups.add(og);
                }
            }

            // Check if local groups, which are selected as online groups, still
            // exist
            // in online store. if they are deleted there, delete them also in
            // local store
            ArrayList<Group> deleteGroups = new ArrayList<Group>();
            for (Group lg : localGroups) {
                boolean found = false;

                // Has online referencesystem id
                if (lg.onlineGroup()) {
                    // Look if group still exists in online referencesystem
                    for (Group og : onlineGroups) {
                        if (lg.getReferenceSystemId().equals(og.getReferenceSystemId())) {
                            found = true;
                        }
                    }

                    // Group no more in online store. remove them also in local
                    // store
                    if (!found) {
                        deleteGroups.add(lg);
                        removeGroupLocalStore(lg, userId);
                    }
                }
            }
            localGroups.removeAll(deleteGroups);
        }

        return localGroups;
    }


    /**
     * Check if the given document is member in an online Group If yes, document
     * is not saved in local store. Reason is, that there are online groups with
     * thousands of documents They should not be saved in local store
     */
    private boolean checkDocumentPublicGroup(Document doc) {
        GroupList gl = (GroupList) doc.getFieldValue(Field.GROUPS);

        boolean isOnline = false;

        if (gl != null) {
            for (Group g : gl) {
                if (g.getType() != null && !g.getType().equals(Group.TYPE_PRIVATE)) {
                    isOnline = true;
                }
            }
        }
        return isOnline;
    }
}
