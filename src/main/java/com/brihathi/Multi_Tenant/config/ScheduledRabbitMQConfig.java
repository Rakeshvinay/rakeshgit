// // package com.brihathi.Multi_Tenant.config;
 
// // import org.springframework.amqp.core.*;
// // import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// // import org.springframework.amqp.rabbit.core.RabbitTemplate;
// // import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
// // import org.springframework.amqp.support.converter.MessageConverter;
// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
 
// // @Configuration
// // public class RabbitMQConfig {
 
// //     public static final String SCHEDULED_AI_ANALYSIS_EXCHANGE = "scheduled_ai_analysis_exchange";
// //     public static final String SCHEDULED_AI_ANALYSIS_ROUTING_KEY = "scheduled_ai.analysis";
 
// //     public static final String SCHEDULED_EXAM_RESULTS_QUEUE = "scheduled_exam_results_queue";
// //     public static final String SCHEDULED_CHAPTER_RESULTS_QUEUE = "scheduled_chapter_results_queue";
// //     public static final String SCHEDULED_SCORE_PREDICTOR_QUEUE = "scheduled_score_predictor_queue";
// //     public static final String SCHEDULED_TIME_ANALYSIS_QUEUE = "scheduled_time_analysis_queue";
// //     public static final String SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE = "scheduled_subject_wise_performance_queue";
// //     public static final String SCHEDULED_SCORE_PROGRESS_QUEUE = "scheduled_score_progress_queue";
// //     public static final String SCHEDULED_LEADERSHIP_BOARD_QUEUE = "scheduled_leadership_board_queue";
// //     // public static final String SCHEDULED_ERROR_TRACKER_QUEUE = "error_tracker";
// //     // public static final String SCHEDULED_ERROR_TRACKING_TRENDS_QUEUE = "error_tracking_trends_queue";
 
 
// //     // Exchange
// //     @Bean
// //     public TopicExchange aiAnalysisExchange() {
// //         return new TopicExchange(SCHEDULED_AI_ANALYSIS_EXCHANGE);
// //     }
 
// //     // Queues
// //     @Bean public Queue ScheduledExamResultsQueue() { return new Queue(SCHEDULED_EXAM_RESULTS_QUEUE); }
// //     @Bean public Queue ScheduledChapterResultsQueue() { return new Queue(SCHEDULED_CHAPTER_RESULTS_QUEUE); }
// //     @Bean public Queue ScheduledScorePredictorQueue() { return new Queue(SCHEDULED_SCORE_PREDICTOR_QUEUE); }
// //     @Bean public Queue ScheduledTimeAnalysisQueue() { return new Queue(SCHEDULED_TIME_ANALYSIS_QUEUE); }
// //     @Bean public Queue ScheduledSubjectWisePerformanceQueue() { return new Queue(SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE); }
// //  //   @Bean public Queue difficultyWisePerformanceQueue() { return new Queue(DIFFICULTY_WISE_PERFORMANCE_QUEUE); }
// //     @Bean public Queue ScheduledScoreProgressQueue() { return new Queue(SCHEDULED_SCORE_PROGRESS_QUEUE); }
// //     @Bean public Queue ScheduledLeadershipBoardQueue() { return new Queue(SCHEDULED_LEADERSHIP_BOARD_QUEUE); }
// //     // @Bean public Queue errorTrackerQueue() { return new Queue(ERROR_TRACKER_QUEUE); }
// //     // @Bean public Queue errorTrackingTrendsQueue() { return new Queue(ERROR_TRACKING_TRENDS_QUEUE);}
 
 
// //     // Bindings (all use same routing key)
// //     @Bean public Binding bindScheduledExamResultsQueue() {
// //         return BindingBuilder.bind(ScheduledExamResultsQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }
 
// //     @Bean public Binding bindScheduledChapterResultsQueue() {
// //         return BindingBuilder.bind(ScheduledChapterResultsQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }
 
// //     @Bean public Binding bindScheduledScorePredictorQueue() {
// //         return BindingBuilder.bind(ScheduledScorePredictorQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }
 
// //     @Bean public Binding bindScheduledTimeAnalysisQueue() {
// //         return BindingBuilder.bind(ScheduledTimeAnalysisQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }
 
// //     @Bean public Binding bindScheduledSubjectWisePerformanceQueue() {
// //         return BindingBuilder.bind(ScheduledSubjectWisePerformanceQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }

 
// //     @Bean public Binding bindScheduledScoreProgressQueue() {
// //         return BindingBuilder.bind(ScheduledScoreProgressQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }
 
// //     @Bean public Binding bindScheduledLeadershipBoardQueue() {
// //         return BindingBuilder.bind(ScheduledLeadershipBoardQueue()).to(aiAnalysisExchange()).with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
// //     }
// //     // @Bean public Binding bindErrorTrackerQueue() {
// //     //     return BindingBuilder.bind(errorTrackerQueue()).to(aiAnalysisExchangeMains()).with(AI_ANALYSIS_ROUTING_KEY);
// //     // }

// //     // @Bean public Binding bindErrorTrackingTrendsQueue() {
// //     //     return BindingBuilder.bind(errorTrackingTrendsQueue()).to(aiAnalysisExchangeMains()).with(AI_ANALYSIS_ROUTING_KEY);
// //     // } 
 
 
// //     // JSON message converter
// //     @Bean
// //     public MessageConverter jsonMessageConverter() {
// //         return new Jackson2JsonMessageConverter();
// //     }
 
// //     // RabbitTemplate with JSON converter
// //     @Bean
// //     public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
// //         RabbitTemplate template = new RabbitTemplate(connectionFactory);
// //         template.setMessageConverter(jsonMessageConverter());
// //         return template;
// //     }
// // }
 
 
// package com.brihathi.Multi_Tenant.config;

// import org.springframework.amqp.core.*;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
// import org.springframework.amqp.support.converter.MessageConverter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class ScheduledRabbitMQConfig {

//     public static final String SCHEDULED_AI_ANALYSIS_EXCHANGE =
//             "scheduled_ai_analysis_exchange";
//             public static final String SCHEDULED_AI_ANALYSIS_ROUTING_KEY = "scheduled_ai.analysis";


//     public static final String SCHEDULED_EXAM_RESULTS_QUEUE =
//             "scheduled_exam_results_queue";
//     public static final String SCHEDULED_CHAPTER_RESULTS_QUEUE =
//             "scheduled_chapter_results_queue";
//     public static final String SCHEDULED_SCORE_PREDICTOR_QUEUE =
//             "scheduled_score_predictor_queue";
//     public static final String SCHEDULED_TIME_ANALYSIS_QUEUE =
//             "scheduled_time_analysis_queue";
//     public static final String SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE =
//             "scheduled_subject_wise_performance_queue";
//     public static final String SCHEDULED_SCORE_PROGRESS_QUEUE =
//             "scheduled_score_progress_queue";
//     public static final String SCHEDULED_LEADERSHIP_BOARD_QUEUE =
//             "scheduled_leadership_board_queue";

//     /* ================= EXCHANGE ================= */

//     @Bean
//     public TopicExchange scheduledAiExchange() {
//         return new TopicExchange(SCHEDULED_AI_ANALYSIS_EXCHANGE);
//     }

//     /* ================= QUEUES ================= */

//     @Bean public Queue scheduledExamResultsQueue() { return new Queue(SCHEDULED_EXAM_RESULTS_QUEUE); }
//     @Bean public Queue scheduledChapterResultsQueue() { return new Queue(SCHEDULED_CHAPTER_RESULTS_QUEUE); }
//     @Bean public Queue scheduledScorePredictorQueue() { return new Queue(SCHEDULED_SCORE_PREDICTOR_QUEUE); }
//     @Bean public Queue scheduledTimeAnalysisQueue() { return new Queue(SCHEDULED_TIME_ANALYSIS_QUEUE); }
//     @Bean public Queue scheduledSubjectWiseQueue() { return new Queue(SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE); }
//     @Bean public Queue scheduledScoreProgressQueue() { return new Queue(SCHEDULED_SCORE_PROGRESS_QUEUE); }
//     @Bean public Queue ScheduledLeadershipBoardQueue() { return new Queue(SCHEDULED_LEADERSHIP_BOARD_QUEUE); }

//     /* ================= BINDINGS ================= */

//     @Bean
//     public Binding examResultsBinding() {
//         return BindingBuilder.bind(scheduledExamResultsQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_EXAM_RESULT");
//     }

//     @Bean
//     public Binding chapterResultsBinding() {
//         return BindingBuilder.bind(scheduledChapterResultsQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_CHAPTER_RESULTS");
//     }

//     @Bean
//     public Binding scorePredictorBinding() {
//         return BindingBuilder.bind(scheduledScorePredictorQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_SCORE_PREDICTOR");
//     }

//     @Bean
//     public Binding timeAnalysisBinding() {
//         return BindingBuilder.bind(scheduledTimeAnalysisQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_TIME_ANALYSIS");
//     }

//     @Bean
//     public Binding subjectWiseBinding() {
//         return BindingBuilder.bind(scheduledSubjectWiseQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_SUBJECT_WISE_PERFORMANCE");
//     }

//     @Bean
//     public Binding scoreProgressBinding() {
//         return BindingBuilder.bind(scheduledScoreProgressQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_SCORE_PROGRESS");
//     }

//     @Bean
//     public Binding leadershipBoardBinding() {
//         return BindingBuilder.bind(ScheduledLeadershipBoardQueue())
//                 .to(scheduledAiExchange())
//                 .with("scheduled_ai.SCHEDULED_LEADERSHIP_BOARD");
//     }

//     /* ================= JSON SUPPORT ================= */

//     @Bean
//     public MessageConverter jsonScheduledMessageConverter() {
//         return new Jackson2JsonMessageConverter();
//     }

//     @Bean
//     public RabbitTemplate rabbitScheduledTemplate(ConnectionFactory cf) {
//         RabbitTemplate template = new RabbitTemplate(cf);
//         template.setMessageConverter(jsonScheduledMessageConverter());
//         return template;
//     }
// }



 
package com.brihathi.Multi_Tenant.config;
 
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class ScheduledRabbitMQConfig {
 
    public static final String SCHEDULED_AI_ANALYSIS_EXCHANGE =
            "scheduled_ai_analysis_exchange";
 
    public static final String SCHEDULED_AI_ANALYSIS_ROUTING_KEY =
            "scheduled_ai.analysis";
 
    public static final String SCHEDULED_EXAM_RESULTS_QUEUE = "scheduled_exam_results_queue";
    public static final String SCHEDULED_CHAPTER_RESULTS_QUEUE = "scheduled_chapter_results_queue";
    public static final String SCHEDULED_SCORE_PREDICTOR_QUEUE = "scheduled_score_predictor_queue";
    public static final String SCHEDULED_TIME_ANALYSIS_QUEUE = "scheduled_time_analysis_queue";
    public static final String SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE = "scheduled_subject_wise_performance_queue";
    public static final String SCHEDULED_SCORE_PROGRESS_QUEUE = "scheduled_score_progress_queue";
    public static final String SCHEDULED_LEADERSHIP_BOARD_QUEUE = "scheduled_leadership_board_queue";
 
    /* ================= EXCHANGE ================= */
 
    @Bean
    public TopicExchange scheduledAiExchange() {
        return new TopicExchange(SCHEDULED_AI_ANALYSIS_EXCHANGE);
    }
 
    /* ================= QUEUES ================= */
 
    @Bean public Queue scheduledExamResultsQueue() { return new Queue(SCHEDULED_EXAM_RESULTS_QUEUE); }
    @Bean public Queue scheduledChapterResultsQueue() { return new Queue(SCHEDULED_CHAPTER_RESULTS_QUEUE); }
    @Bean public Queue scheduledScorePredictorQueue() { return new Queue(SCHEDULED_SCORE_PREDICTOR_QUEUE); }
    @Bean public Queue scheduledTimeAnalysisQueue() { return new Queue(SCHEDULED_TIME_ANALYSIS_QUEUE); }
    @Bean public Queue scheduledSubjectWiseQueue() { return new Queue(SCHEDULED_SUBJECT_WISE_PERFORMANCE_QUEUE); }
    @Bean public Queue scheduledScoreProgressQueue() { return new Queue(SCHEDULED_SCORE_PROGRESS_QUEUE); }
    @Bean public Queue scheduledLeadershipBoardQueue() { return new Queue(SCHEDULED_LEADERSHIP_BOARD_QUEUE); }
 
    /* ================= BINDINGS (ONE ROUTING KEY) ================= */
 
    @Bean
    public Binding scheduledExamResultsBinding() {
        return BindingBuilder.bind(scheduledExamResultsQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean
    public Binding scheduledChapterResultsBinding() {
        return BindingBuilder.bind(scheduledChapterResultsQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean
    public Binding scheduledScorePredictorBinding() {
        return BindingBuilder.bind(scheduledScorePredictorQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean
    public Binding scheduledTimeAnalysisBinding() {
        return BindingBuilder.bind(scheduledTimeAnalysisQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean
    public Binding scheduledSubjectWiseBinding() {
        return BindingBuilder.bind(scheduledSubjectWiseQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean
    public Binding scheduledScoreProgressBinding() {
        return BindingBuilder.bind(scheduledScoreProgressQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean
    public Binding scheduledLeadershipBoardBinding() {
        return BindingBuilder.bind(scheduledLeadershipBoardQueue())
                .to(scheduledAiExchange())
                .with(SCHEDULED_AI_ANALYSIS_ROUTING_KEY);
    }
}
 
 
