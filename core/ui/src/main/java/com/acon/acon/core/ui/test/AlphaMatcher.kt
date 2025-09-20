package com.acon.acon.core.ui.test

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.test.SemanticsNodeInteraction

val TestAlphaKey = SemanticsPropertyKey<Float>("TestAlpha")
var SemanticsPropertyReceiver.testAlpha by TestAlphaKey

/**
 * [TestAlphaKey] 속성이 설정된 시맨틱 노드의 알파(alpha) 값을 가져옵니다.
 *
 * 이 함수는 UI 테스트에서 컴포저블의 알파(투명도) 값을 확인하는 데 사용됩니다.
 * 테스트 대상 컴포저블에 `Modifier.semantics { testAlpha = alphaValue }`와 같이
 * `testAlpha` 시맨틱 속성이 설정되어 있어야 합니다.
 *
 * @return 시맨틱 노드의 알파 값을 [Float] 타입으로 반환합니다.
 * @throws AssertionError 시맨틱 노드에 `TestAlphaKey`가 설정되어 있지 않은 경우 발생합니다.
 * @see TestAlphaKey
 * @see testAlpha
 */
fun SemanticsNodeInteraction.getAlpha(): Float {
    val node = fetchSemanticsNode("Failed to get alpha value.")

    if (!node.config.contains(TestAlphaKey)) {
        throw AssertionError("TestAlphaKey를 찾을 수 없습니다. " +
                "Modifier.semantics { testAlpha = ... }를 지정하셨나요 ?")
    }

    return node.config[TestAlphaKey]
}
