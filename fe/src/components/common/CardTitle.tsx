import { PauseCircleOutlined, LoadingOutlined } from "@ant-design/icons";
import { Col, Row, Tooltip } from "antd";
import { useMemo } from "react";
import { ActionStatus } from "../../api/types";

interface FBCardTitleProps {
  title: string;
  statusIcon?: ActionStatus;
  statusTitles?: Map<ActionStatus, JSX.Element>;
}

export const FBCardTitle: React.FC<FBCardTitleProps> = ({
  title,
  statusIcon = ActionStatus.INVISIBLE,
  statusTitles,
}) => {
  const titleNode = useMemo(
    () => <div className="fb-card-title">{title}</div>,
    [title]
  );

  if (statusIcon === ActionStatus.INVISIBLE) {
    return titleNode;
  }

  return (
    <Row justify={"space-between"}>
      <Col className="fb-card-title">{title}</Col>
      <Col
        className={
          statusIcon === ActionStatus.PAUSED ? "color-red-5" : "color-blue-5"
        }
      >
        <Tooltip placement="top" title={statusTitles?.get(statusIcon)}>
          {statusIcon === ActionStatus.PAUSED ? (
            <PauseCircleOutlined />
          ) : (
            <LoadingOutlined />
          )}
        </Tooltip>
      </Col>
    </Row>
  );
};
