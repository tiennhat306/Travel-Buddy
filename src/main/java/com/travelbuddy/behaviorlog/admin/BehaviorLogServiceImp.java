package com.travelbuddy.behaviorlog.admin;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BehaviorLogServiceImp implements BehaviorLogService {
}
