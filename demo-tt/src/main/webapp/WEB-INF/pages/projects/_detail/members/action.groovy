package com.manydesigns.portofino.pageactions.crud

import com.manydesigns.portofino.tt.TtUtils

import com.manydesigns.elements.ElementsThreadLocals
import com.manydesigns.elements.blobs.Blob
import com.manydesigns.elements.blobs.BlobManager
import com.manydesigns.elements.forms.Form
import com.manydesigns.elements.servlet.ServletUtils
import com.manydesigns.portofino.buttons.GuardType
import com.manydesigns.portofino.buttons.annotations.Button
import com.manydesigns.portofino.buttons.annotations.Buttons
import com.manydesigns.portofino.buttons.annotations.Guard
import com.manydesigns.portofino.security.AccessLevel
import com.manydesigns.portofino.security.RequiresPermissions
import com.manydesigns.portofino.security.SupportsPermissions
import javax.servlet.http.HttpServletResponse
import net.sourceforge.stripes.action.Before
import net.sourceforge.stripes.action.RedirectResolution
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.StreamingResolution
import org.apache.shiro.SecurityUtils

@SupportsPermissions([ CrudAction.PERMISSION_CREATE, CrudAction.PERMISSION_EDIT, CrudAction.PERMISSION_DELETE ])
@RequiresPermissions(level = AccessLevel.VIEW)
class ProjectMembersAction extends CrudAction {

    Serializable project;
    Object old;

    @Before
    public void prepareProject() {
        project = ElementsThreadLocals.getOgnlContext().get("project");
    }

    //**************************************************************************
    // Role checking
    //**************************************************************************

    public boolean isViewer() {
        return TtUtils.principalHasProjectRole(project, TtUtils.ROLE_VIEWER);
    }

    public boolean isManager() {
        return TtUtils.principalHasProjectRole(project, TtUtils.ROLE_MANAGER);
    }

    //**************************************************************************
    // Create customizations
    //**************************************************************************

    @Override
    @Button(list = "crud-search", key = "create.new", order = 1d, type = Button.TYPE_SUCCESS,
            icon = "icon-plus icon-white", group = "crud")
    @Guard(test="isManager()", type=GuardType.VISIBLE)
    Resolution create() {
        return super.create()
    }

    @Override
    @Button(list = "crud-create", key = "save", order = 1d, type = Button.TYPE_PRIMARY)
    @Guard(test="isManager()", type=GuardType.VISIBLE)
    Resolution save() {
        return super.save() 
    }

    protected void createSetup(Object object) {
        object.project = project.id;
        object.notifications = false;
    }

    protected void createPostProcess(Object object) {
        Object principal = SecurityUtils.subject.principal;
        Object user2 = session.load("users", object.user_);
        Date now = new Date();
        TtUtils.addActivity(session,
                principal,
                now,
                TtUtils.ACTIVITY_TYPE_MEMBER_CREATED,
                null,
                user2,
                project,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    //**************************************************************************
    // Edit customizations
    //**************************************************************************

    @Override
    @Buttons([
        @Button(list = "crud-read", key = "edit", order = 1d, icon = "icon-edit icon-white",
                group = "crud", type = Button.TYPE_SUCCESS),
        @Button(list = "crud-read-default-button", key = "search")
    ])
    @Guard(test="isManager()", type=GuardType.VISIBLE)
    Resolution edit() {
        return super.edit()    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @Button(list = "crud-edit", key = "update", order = 1d, type = Button.TYPE_PRIMARY)
    @Guard(test="isManager()", type=GuardType.VISIBLE)
    Resolution update() {
        return super.update()    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void editSetup(Object object) {
        old = object.clone();
    }

    protected void editPostProcess(Object object) {
        Object principal = SecurityUtils.subject.principal;
        Form newForm = form;
        form = buildForm(formBuilder);
        form.readFromObject(old);
        String message = TtUtils.createDiffMessage(form, newForm);
        if (message != null) {
            Date now = new Date();
            TtUtils.addActivity(session,
                    principal,
                    now,
                    TtUtils.ACTIVITY_TYPE_MEMBER_UPDATED,
                    message,
                    object.fk_member_user,
                    project,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }


    //**************************************************************************
    // Delete customizations
    //**************************************************************************

    @Override
    @Button(list = "crud-read", key = "delete", order = 2d, icon = Button.ICON_TRASH, group = "crud")
    @Guard(test = "isManager()", type = GuardType.VISIBLE)
    public Resolution delete() {
        return super.delete();
    }

    protected void deletePostProcess(Object object) {
        Object principal = SecurityUtils.subject.principal;
        Date now = new Date();
        TtUtils.addActivity(session,
                principal,
                now,
                TtUtils.ACTIVITY_TYPE_MEMBER_DELETED,
                null,
                object.fk_member_user,
                project,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    //**************************************************************************
    // Bulk edit customizations
    //**************************************************************************

    @Override
    @Button(list = "crud-search", key = "edit", order = 2d, icon = Button.ICON_EDIT, group = "crud")
    @Guard(test = "isBulkOperationsEnabled() && isManager()", type = GuardType.VISIBLE)
    Resolution bulkEdit() {
        return super.bulkEdit();
    }

    @Button(list = "crud-bulk-edit", key = "update", order = 1d, type = Button.TYPE_PRIMARY)
    @Guard(test = "isManager()", type = GuardType.VISIBLE)
    Resolution bulkUpdate() {
        return super.bulkUpdate();
    }

    //**************************************************************************
    // Bulk delete customizations
    //**************************************************************************

    @Button(list = "crud-search", key = "delete", order = 3d, icon = Button.ICON_TRASH, group = "crud")
    @Guard(test = "isBulkOperationsEnabled() && isManager()", type = GuardType.VISIBLE)
    public Resolution bulkDelete() {
        return super.bulkDelete();
    }


    //**************************************************************************
    // member image
    //**************************************************************************

    private final static MEMBER_HQL = """
    select u
    from users u
    join u.fk_member_user m
    where m.project = :project_id
    and u.id = :user_id
    """;

    private Long userId;

    public Resolution userImage() {
        if(userId == null) {
            return new RedirectResolution("/images/user-placeholder-40x40.png");
        }
        Map user = (Map) session.createQuery(MEMBER_HQL)
                .setString("project_id", project.id)
                .setLong("user_id", userId)
                .uniqueResult();
        if(user == null || user.avatar == null) {
            return new RedirectResolution("/images/user-placeholder-40x40.png");
        } else {
            BlobManager mgr = ElementsThreadLocals.blobManager;
            Blob blob = mgr.loadBlob(user.avatar);
            long contentLength = blob.getSize();
            String contentType = blob.getContentType();
            InputStream inputStream = new FileInputStream(blob.getDataFile());

            //Cache blobs (they're immutable)
            HttpServletResponse response = context.getResponse();
            ServletUtils.markCacheableForever(response);

            return new StreamingResolution(contentType, inputStream)
                    .setLength(contentLength)
                    .setLastModified(blob.getCreateTimestamp().getMillis());
        }
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

}