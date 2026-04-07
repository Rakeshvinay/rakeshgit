// package com.brihathi.Multi_Tenant.producer;
 
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.stereotype.Component;
// import com.brihathi.Multi_Tenant.dto.MessageDTO;
// import com.brihathi.Multi_Tenant.config.RabbitMQConfig;
 
// @Component
// public class MessageProducer {
 
//     private final RabbitTemplate rabbitTemplate;
 
//     public MessageProducer(RabbitTemplate rabbitTemplate) {
//         this.rabbitTemplate = rabbitTemplate;
//     }
 
//     public void sendMessage(MessageDTO dto) {
//         rabbitTemplate.convertAndSend(
//             RabbitMQConfig.SCHEDULED_AI_ANALYSIS_EXCHANGE,
//             RabbitMQConfig.SCHEDULED_AI_ANALYSIS_ROUTING_KEY,
//             dto
//         );
//         // System.out.println("✅ Sent message with type: " + dto.getType());
//     }
 
//     public void sendAllMessagesBatch(Long userId, Long eduScheduledExamId) {
//         sendMessage(new MessageDTO("SCHEDULED_EXAM_RESULTS", userId, eduScheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_CHAPTER_RESULTS", userId, eduScheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_SCORE_PREDICTOR", userId, eduScheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_TIME_ANALYSIS", userId, eduScheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_SUBJECT_WISE_PERFORMANCE", userId, eduScheduledExamId));
// //        sendMessage(new MessageDTO("DIFFICULTY_WISE_PERFORMANCE", userId, scheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_SCORE_PROGRESS", userId, eduScheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_LEADERSHIP_BOARD", userId, eduScheduledExamId));
//         sendMessage(new MessageDTO("SCHEDULED_ERROR_TRACKER", userId, eduScheduledExamId));
 
//     }
// }
 
 
// package com.brihathi.Multi_Tenant.producer;

// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.stereotype.Component;

// import com.brihathi.Multi_Tenant.config.ScheduledRabbitMQConfig;
// import com.brihathi.Multi_Tenant.dto.ScheduledMessageDTO;
// import java.util.List;
// @Component
// public class ScheduledMessageProducer {

//     private final RabbitTemplate rabbitTemplate;

//     public ScheduledMessageProducer(RabbitTemplate rabbitTemplate) {
//         this.rabbitTemplate = rabbitTemplate;
//     }

//     public void sendScheduledAnalyticsMessages(Long userId, Long eduScheduledExamId, Long tenantId) {

//         List<String> types = List.of(
//                 "SCHEDULED_EXAM_RESULT",
//                 "SCHEDULED_CHAPTER_RESULTS",
//                 "SCHEDULED_SCORE_PREDICTOR",
//                 "SCHEDULED_TIME_ANALYSIS",
//                 "SCHEDULED_SUBJECT_WISE_PERFORMANCE",
//                 "SCHEDULED_SCORE_PROGRESS",
//                 "SCHEDULED_LEADERSHIP_BOARD"
//         );

//         for (String type : types) {

//             String routingKey = "scheduled_ai." + type;

//             System.out.println("📤 SENDING → " + type +
//                     " | user=" + userId +
//                     " | exam=" + eduScheduledExamId +
//                     " | tenant=" + tenantId);

//             rabbitTemplate.convertAndSend(
//                     ScheduledRabbitMQConfig.SCHEDULED_AI_ANALYSIS_EXCHANGE,
//                     routingKey,
//                     new ScheduledMessageDTO(type, userId, eduScheduledExamId, tenantId)
//             );
//         }
//     }
// }


 
package com.brihathi.Multi_Tenant.producer;
 
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.brihathi.Multi_Tenant.config.ScheduledRabbitMQConfig;
import com.brihathi.Multi_Tenant.dto.ScheduledMessageDTO;
import java.util.List;
 
@Component
public class ScheduledMessageProducer {
 
    private final RabbitTemplate rabbitTemplate;
 
    public ScheduledMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
 
    public void sendScheduledAnalyticsMessages(Long userId,
                                               Long eduScheduledExamId,
                                               Long tenantId) {
 
        List<String> types = List.of(
                "SCHEDULED_EXAM_RESULT",
                "SCHEDULED_CHAPTER_RESULTS",
                "SCHEDULED_SCORE_PREDICTOR",
                "SCHEDULED_TIME_ANALYSIS",
                "SCHEDULED_SUBJECT_WISE_PERFORMANCE",
                "SCHEDULED_SCORE_PROGRESS",
                "SCHEDULED_LEADERSHIP_BOARD"
        );
 
        for (String type : types) {
 
            rabbitTemplate.convertAndSend(
                    ScheduledRabbitMQConfig.SCHEDULED_AI_ANALYSIS_EXCHANGE,
                    ScheduledRabbitMQConfig.SCHEDULED_AI_ANALYSIS_ROUTING_KEY,
                    new ScheduledMessageDTO(type, userId, eduScheduledExamId, tenantId)
            );
        }
    }
}
 
 