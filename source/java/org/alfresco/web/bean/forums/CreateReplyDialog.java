package org.alfresco.web.bean.forums;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.ui.common.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bean implementation of the "Create Reply Dialog".
 * 
 * @author gavinc
 */
public class CreateReplyDialog extends CreatePostDialog
{
   protected String replyContent = null;
   
   private static final Log logger = LogFactory.getLog(CreateReplyDialog.class);

   // ------------------------------------------------------------------------------
   // Wizard implementation
   
   @Override
   public void init(Map<String, String> parameters)
   {
      super.init(parameters);
      
      this.replyContent = null;
   }
   
   @Override
   protected String finishImpl(FacesContext context, String outcome) throws Exception
   {
      // remove link breaks and replace with <br/>
      this.content = Utils.replaceLineBreaks(this.content);
      
      super.finishImpl(context, outcome);
      
      // setup the referencing aspect with the references association
      // between the new post and the one being replied to
      this.nodeService.addAspect(this.createdNode, ContentModel.ASPECT_REFERENCING, null);
      this.nodeService.createAssociation(this.createdNode, this.browseBean.getDocument().getNodeRef(), 
            ContentModel.ASSOC_REFERENCES);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("created new node: " + this.createdNode);
         logger.debug("existing node: " + this.browseBean.getDocument().getNodeRef());
      }
      
      return outcome;
   }
   
   @Override
   public String getFinishButtonLabel()
   {
      return Application.getMessage(FacesContext.getCurrentInstance(), "reply");
   }

   // ------------------------------------------------------------------------------
   // Bean Getters and Setters
   
   /**
    * Returns the content of the post we are replying to
    * 
    * @return The content
    */
   public String getReplyContent()
   {
      if (this.replyContent == null)
      {
         // get the content reader of the node we are replying to
         NodeRef replyNode = this.browseBean.getDocument().getNodeRef();
         if (replyNode != null)
         {
            ContentReader reader = this.contentService.getReader(replyNode, ContentModel.PROP_CONTENT);
            
            if (reader != null)
            {
               this.replyContent = reader.getContentString();
            }
         }
      }
      
      return this.replyContent;
   }
}
