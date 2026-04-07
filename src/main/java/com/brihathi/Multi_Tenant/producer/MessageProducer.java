package com.brihathi.Multi_Tenant.producer;
 
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.brihathi.Multi_Tenant.config.RabbitMQConfig;
import com.brihathi.Multi_Tenant.dto.MessageDTO;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
 
@Component
public class MessageProducer {
 
    private final RabbitTemplate rabbitTemplate;
 
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
 
    public void sendMessage(MessageDTO dto) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.AI_ANALYSIS_EXCHANGE,
            RabbitMQConfig.AI_ANALYSIS_ROUTING_KEY,
            dto
        );
        // System.out.println("✅ Sent message with type: " + dto.getType());
    }
 
    /**
     * Send a batch of messages only AFTER the surrounding DB transaction commits,
     * so Rabbit consumers always see the final state in exam_results.
     */
    public void sendMessagesAfterCommit(java.util.List<MessageDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return;

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // Not in a TX => send immediately
            for (MessageDTO dto : dtos) {
                sendMessage(dto);
            }
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (MessageDTO dto : dtos) {
                    sendMessage(dto);
                }
            }
        });
    }

    public void sendAllMessagesBatch(Long userId, Long examId) {
        sendMessagesAfterCommit(java.util.List.of(
            new MessageDTO("EXAM_RESULT", userId, examId),
            new MessageDTO("CHAPTER_RESULTS", userId, examId),
            new MessageDTO("SCORE_PREDICTOR", userId, examId),
            new MessageDTO("TIME_ANALYSIS", userId, examId),
            new MessageDTO("SUBJECT_WISE_PERFORMANCE", userId, examId),
//          new MessageDTO("DIFFICULTY_WISE_PERFORMANCE", userId, examId),
            new MessageDTO("SCORE_PROGRESS", userId, examId),
            new MessageDTO("LEADERSHIP_BOARD", userId, examId),
            new MessageDTO("ERROR_TRACKER", userId, examId),
            new MessageDTO("MYSTERY_BOX", userId, examId)
        ));
        //  sendMessage(new MessageDTO("ERROR_TRACKING_TRENDS", userId, examId));

    }
}
 
 
 